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
import org.dimdev.dimdoors.api.util.NbtUtil;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.util.schematic.Schematic;

public class PocketLoader implements SimpleSynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final PocketLoader INSTANCE = new PocketLoader();
	private Map<Identifier, PocketGenerator> pocketGeneratorMap = new ConcurrentHashMap<>();
	private Map<Identifier, VirtualPocket> pocketGroups = new ConcurrentHashMap<>();
	private Map<Identifier, PocketTemplate> templates = new ConcurrentHashMap<>();
	private Map<Identifier, CompoundTag> riftDataMap = new ConcurrentHashMap<>();

	private PocketLoader() {
	}

	@Override
	public void apply(ResourceManager manager) {
		pocketGeneratorMap.clear();
		pocketGroups.clear();
		templates.clear();
		riftDataMap.clear();

		CompletableFuture<Map<Identifier, CompoundTag>> futureRiftDataMap = loadResourcePathFromJsonToMap(manager, "pockets/json", t -> NbtUtil.asCompoundTag(t, "Could not load rift data since its json does not represent a CompoundTag!"));
		CompletableFuture<Map<Identifier, PocketGenerator>> futurePocketGeneratorMap = loadResourcePathFromJsonToMap(manager, "pockets/generators", this::loadPocketGenerator);
		CompletableFuture<Map<Identifier, VirtualPocket>> futurePocketGroups = loadResourcePathFromJsonToMap(manager, "pockets/groups", this::loadPocketGroup);
		CompletableFuture<Map<Identifier, PocketTemplate>> futureTemplates = loadResourcePathFromCompressedNbtToMap(manager, "pockets/schematic", ".schem", this::loadPocketTemplate);

		riftDataMap = futureRiftDataMap.join();
		pocketGeneratorMap = futurePocketGeneratorMap.join();
		pocketGroups = futurePocketGroups.join();
		templates = futureTemplates.join();
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

	private <T> CompletableFuture<Map<Identifier, T>> loadResourcePathFromCompressedNbtToMap(ResourceManager manager, String startingPath, String extension, Function<CompoundTag, T> reader) {
		int sub = startingPath.endsWith("/") ? 0 : 1;

		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(extension));
		return CompletableFuture.supplyAsync(() ->
				ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
						id -> new Identifier(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
						id -> {
							try {
								return reader.apply(NbtIo.readCompressed(manager.getResource(id).getInputStream()));
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

	public Tag getRiftDataTag(String id) {
		return this.riftDataMap.get(new Identifier(id));
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
		return new WeightedList<>(pocketGeneratorMap.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(Identifier group) {
		return pocketGroups.get(group);
	}

	public static PocketLoader getInstance() {
		return INSTANCE;
	}

	public Map<Identifier, PocketTemplate> getTemplates() {
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
