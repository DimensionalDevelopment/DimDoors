package org.dimdev.dimdoors.pockets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.NbtUtil;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.util.schematic.Schematic;

public class PocketLoader implements SimpleSynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final PocketLoader INSTANCE = new PocketLoader();
	private SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
	private SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
	private SimpleTree<String, Tag> dataTree = new SimpleTree<>(String.class);

	private PocketLoader() {
	}

	@Override
	public void apply(ResourceManager manager) {
		pocketGenerators.clear();
		pocketGroups.clear();
		templates.clear();
		dataTree.clear();

		dataTree = loadResourcePathFromJsonToTree(manager, "pockets/json", t -> t).join();

		CompletableFuture<SimpleTree<String, PocketGenerator>> futurePocketGeneratorMap = loadResourcePathFromJsonToTree(manager, "pockets/generators", this::loadPocketGenerator);
		CompletableFuture<SimpleTree<String, VirtualPocket>> futurePocketGroups = loadResourcePathFromJsonToTree(manager, "pockets/groups", this::loadPocketGroup);
		CompletableFuture<SimpleTree<String, PocketTemplate>> futureTemplates = loadResourcePathFromCompressedNbtToTree(manager, "pockets/schematic", ".schem", this::loadPocketTemplate);


		pocketGenerators = futurePocketGeneratorMap.join();
		pocketGroups = futurePocketGroups.join();
		templates = futureTemplates.join();

		pocketGroups.forEach((path, value) -> System.out.println(path.toString() + ": " + value.toString()));
	}

	private <T> CompletableFuture<SimpleTree<String, T>> loadResourcePathFromJsonToTree(ResourceManager manager, String startingPath, Function<Tag, T> reader) {
		int sub = startingPath.endsWith("/") ? 0 : 1;

		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(".json"));
		return CompletableFuture.supplyAsync(() -> {
			SimpleTree<String, T> tree = new SimpleTree<>(String.class);
			tree.putAll(ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
					id -> Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
					id -> {
						try {
							JsonElement json = GSON.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonElement.class);
							return reader.apply(JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json));
						} catch (IOException e) {
							throw new RuntimeException("Error loading resource: " + id);
						}
					})));
			return tree;
		});
	}

	private <T> CompletableFuture<SimpleTree<String, T>> loadResourcePathFromCompressedNbtToTree(ResourceManager manager, String startingPath, String extension, Function<CompoundTag, T> reader) {
		int sub = startingPath.endsWith("/") ? 0 : 1;

		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(extension));
		return CompletableFuture.supplyAsync(() -> {
			SimpleTree<String, T> tree = new SimpleTree<>(String.class);
			tree.putAll(ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
					id -> Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
					id -> {
						try {
							return reader.apply(NbtIo.readCompressed(manager.getResource(id).getInputStream()));
						} catch (IOException e) {
							throw new RuntimeException("Error loading resource: " + id);
						}
					})));
			return tree;
		});
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

	public Tag getDataTag(String id) {
		return this.dataTree.get(Path.stringPath(id));
	}

	public CompoundTag getDataCompoundTag(String id) {
		return NbtUtil.asCompoundTag(getDataTag(id), "Could not convert Tag \"" + id + "\" to CompoundTag!");
	}

	private VirtualPocket loadPocketGroup(Tag tag) {
		return VirtualPocket.deserialize(tag);
	}

	private PocketGenerator loadPocketGenerator(Tag tag) {
		return PocketGenerator.deserialize(NbtUtil.asCompoundTag(tag, "Could not load PocketGenerator since its json does not represent a CompoundTag!"));
	}

	private PocketTemplate loadPocketTemplate(CompoundTag tag) {
		try {
			return new PocketTemplate(Schematic.fromTag(tag));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + tag.toString(), e);
		}
	}

	public WeightedList<PocketGenerator, PocketGenerationContext> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
		return new WeightedList<>(pocketGenerators.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(Identifier group) {
		return pocketGroups.get(Path.stringPath(group));
	}

	public static PocketLoader getInstance() {
		return INSTANCE;
	}

	public SimpleTree<String, PocketTemplate> getTemplates() {
		return this.templates;
	}

	public SimpleTree<String, VirtualPocket> getPocketGroups() {
		return this.pocketGroups;
	}

	public PocketGenerator getGenerator(Identifier id) {
		return pocketGenerators.get(Path.stringPath(id));
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("dimdoors", "schematics_v2");
	}
}
