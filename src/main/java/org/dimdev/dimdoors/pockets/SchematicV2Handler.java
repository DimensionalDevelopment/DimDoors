package org.dimdev.dimdoors.pockets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.WeightedList;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;

public class SchematicV2Handler implements SimpleSynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final SchematicV2Handler INSTANCE = new SchematicV2Handler();
	private Map<Identifier, PocketGenerator> pocketGeneratorMap = new ConcurrentHashMap<>();
	private Map<Identifier, VirtualPocket> pocketGroups = new ConcurrentHashMap<>();
	private Map<Identifier, PocketTemplateV2> templates = new ConcurrentHashMap<>();

	private SchematicV2Handler() {
	}

	@Override
	public void apply(ResourceManager manager) {
		pocketGeneratorMap.clear();
		pocketGroups.clear();
		templates.clear();

		CompletableFuture<Map<Identifier, PocketGenerator>> futurePocketGeneratorMap = loadResourcePathFromJsonToMap(manager, "pockets/generators", this::loadPocketGenerator);
		CompletableFuture<Map<Identifier, VirtualPocket>> futurePocketGroups = loadResourcePathFromJsonToMap(manager, "pockets/groups", this::loadPocketGroup);

		try {
			pocketGeneratorMap = futurePocketGeneratorMap.get();
			pocketGroups = futurePocketGroups.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}

	}

	private <T> CompletableFuture<Map<Identifier, T>> loadResourcePathFromJsonToMap(ResourceManager manager, String startingPath, Function<Tag, T> reader) {
		int sub = startingPath.endsWith("/") ? 0 : 1;

		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(".json"));
		return CompletableFuture.supplyAsync(() ->
				ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
						id -> new Identifier(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
						id -> {
							try {
								JsonElement json = GSON.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonElement.class);
								return reader.apply(JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json));
							} catch (IOException e) {
								throw new RuntimeException("Error loading resource: " + id);
							}
						})));
	}

//    public void load() {
//        long startTime = System.currentTimeMillis();
//
//		try {
//			Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/generators").toURI());
//			loadJson(path, new String[0], this::loadPocketGenerator);
//			LOGGER.info("Loaded pockets in {} seconds", System.currentTimeMillis() - startTime);
//		} catch (URISyntaxException e) {
//			LOGGER.error(e);
//		}
//
//		startTime = System.currentTimeMillis();
//		try {
//			Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/groups").toURI());
//			loadJson(path, new String[0], this::loadPocketGroup);
//			LOGGER.info("Loaded pocket groups in {} seconds", System.currentTimeMillis() - startTime);
//		} catch (URISyntaxException e) {
//			LOGGER.error(e);
//		}
//    }

	public Tag readNbtFromJson(String id) {
		try {
			Path path = Paths.get(SchematicV2Handler.class.getResource("/resourcepacks/default_pockets/data/dimdoors/pockets/json/" + id + ".json").toURI());
			if (!Files.isRegularFile(path)) {
				return null;
			}
			try {
				JsonElement json = GSON.fromJson(String.join("", Files.readAllLines(path)), JsonElement.class);
				return JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		}
		return null;
	}

	private VirtualPocket loadPocketGroup(Tag tag) {
		return VirtualPocket.deserialize(tag);
	}

	private PocketGenerator loadPocketGenerator(Tag tag) {
		if (tag == null || tag.getType() != NbtType.COMPOUND) {
			throw new RuntimeException("Could not load PocketGenerator since its json does not represent a CompoundTag!");
		}
		return PocketGenerator.deserialize((CompoundTag) tag);
	}

	public void loadSchematic(Identifier templateID, String id) {
		try {
			if (templates.containsKey(templateID)) return;
			Path schemPath = Paths.get(SchematicV2Handler.class.getResource(String.format("/resourcepacks/default_pockets/data/dimdoors/pockets/schematic/%s.schem", id.replaceAll("\\.", "/"))).toURI());
			CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
			Schematic schematic = Schematic.fromTag(schemTag);
			PocketTemplateV2 template = new PocketTemplateV2(schematic, id);
			templates.put(templateID, template);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load schematic!", e);
		}
	}

	public WeightedList<PocketGenerator, PocketGenerationParameters> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
		return new WeightedList<>(pocketGeneratorMap.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(Identifier group) {
		return pocketGroups.get(group);
	}

	public static SchematicV2Handler getInstance() {
		return INSTANCE;
	}

	public Map<Identifier, PocketTemplateV2> getTemplates() {
		return this.templates;
	}

	public Map<Identifier, VirtualPocket> getPocketGroups() {
		return this.pocketGroups;
	}

	public PocketGenerator getGenerator(Identifier id) {
		return pocketGeneratorMap.get(id);
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("dimdoors", "schematics_v2");
	}
}
