package org.dimdev.dimdoors.pockets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PocketLoader implements ResourceManagerReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PocketLoader INSTANCE = new PocketLoader();
	private SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> virtualPockets = new SimpleTree<>(String.class);
	private SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
	private SimpleTree<String, Tag> dataTree = new SimpleTree<>(String.class);

	private PocketLoader() {
	}

	public void dump() {
		virtualPockets.forEach((path, pocketGenerator) -> LOGGER.info("Virtual Pocket: " + path + " -> " + pocketGenerator.toString()));
		pocketGroups.forEach((path, pocketGenerator) -> LOGGER.info("Pocket Group: " + path + " -> " + pocketGenerator.toString()));
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		pocketGenerators.clear();
		pocketGroups.clear();
		virtualPockets.clear();
		templates.clear();
		dataTree.clear();

		dataTree = ResourceUtil.loadResourcePathToMap(manager, "pockets/json", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.composeIdentity(), ResourceUtil.PATH_KEY_PROVIDER).join();

		CompletableFuture<SimpleTree<String, PocketGenerator>> futurePocketGeneratorMap = ResourceUtil.loadResourcePathToMap(manager, "pockets/generators", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(pocketGeneratorLoader(manager)), ResourceUtil.PATH_KEY_PROVIDER);
		CompletableFuture<SimpleTree<String, VirtualPocket>> futurePocketGroups = ResourceUtil.loadResourcePathToMap(manager, "pockets/groups", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(virtualPocketLoader(manager)), ResourceUtil.PATH_KEY_PROVIDER);
		CompletableFuture<SimpleTree<String, VirtualPocket>> futureVirtualPockets = ResourceUtil.loadResourcePathToMap(manager, "pockets/virtual", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(virtualPocketLoader(manager)), ResourceUtil.PATH_KEY_PROVIDER);
		CompletableFuture<SimpleTree<String, PocketTemplate>> futureTemplates = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(this::loadPocketTemplate), ResourceUtil.PATH_KEY_PROVIDER);


		pocketGenerators = futurePocketGeneratorMap.join();
		pocketGroups = futurePocketGroups.join();
		virtualPockets = futureVirtualPockets.join();
		templates = futureTemplates.join();

		pocketGroups.values().forEach(VirtualPocket::init);
		virtualPockets.values().forEach(VirtualPocket::init);
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

	public Tag getDataNbt(String id) {
		return this.dataTree.get(Path.stringPath(id));
	}

	public CompoundTag getDataNbtCompound(String id) {
		return NbtUtil.asNbtCompound(getDataNbt(id), "Could not convert Tag \"" + id + "\" to CompoundTag!");
	}

	private BiFunction<Tag, Path<String>, VirtualPocket> virtualPocketLoader(ResourceManager manager) {
		return (nbt, ignore) -> VirtualPocket.deserialize(nbt, manager);
	}

	private BiFunction<Tag, Path<String>, PocketGenerator> pocketGeneratorLoader(ResourceManager manager) {
		return (nbt, ignore) -> PocketGenerator.deserialize(NbtUtil.asNbtCompound(nbt, "Could not load PocketGenerator since its json does not represent an CompoundTag!"), manager);
	}

	private PocketTemplate loadPocketTemplate(CompoundTag nbt, Path<String> id) {
		try {
			return new PocketTemplate(Schematic.fromNbt(nbt), ResourceLocation.tryParse(id.reduce(String::concat).orElseThrow()));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + id.toString(), e);
		}
	}

	public WeightedList<PocketGenerator, PocketGenerationContext> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
		return new WeightedList<>(pocketGenerators.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(ResourceLocation group) {
		return pocketGroups.get(Path.stringPath(group));
	}

	public VirtualPocket getVirtual(ResourceLocation id) {
		return virtualPockets.get(Path.stringPath(id));
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

	public SimpleTree<String, VirtualPocket> getVirtualPockets() {
		return this.virtualPockets;
	}

	public PocketGenerator getGenerator(ResourceLocation id) {
		return pocketGenerators.get(Path.stringPath(id));
	}
}
