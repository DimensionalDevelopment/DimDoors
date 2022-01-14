package org.dimdev.dimdoors.pockets;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.util.schematic.Schematic;

public class PocketLoader implements SimpleSynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PocketLoader INSTANCE = new PocketLoader();
	private SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> virtualPockets = new SimpleTree<>(String.class);
	private SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
	private SimpleTree<String, NbtElement> dataTree = new SimpleTree<>(String.class);

	private PocketLoader() {
	}

	@Override
	public void reload(ResourceManager manager) {
		pocketGenerators.clear();
		pocketGroups.clear();
		virtualPockets.clear();
		templates.clear();
		dataTree.clear();

		dataTree = ResourceUtil.loadResourcePathToMap(manager, "pockets/json", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.composeIdentity(), ResourceUtil.PATH_KEY_PROVIDER).join();

		CompletableFuture<SimpleTree<String, PocketGenerator>> futurePocketGeneratorMap = ResourceUtil.loadResourcePathToMap(manager, "pockets/generators", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(this::loadPocketGenerator), ResourceUtil.PATH_KEY_PROVIDER);
		CompletableFuture<SimpleTree<String, VirtualPocket>> futurePocketGroups = ResourceUtil.loadResourcePathToMap(manager, "pockets/groups", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(this::loadVirtualPocket), ResourceUtil.PATH_KEY_PROVIDER);
		CompletableFuture<SimpleTree<String, VirtualPocket>> futureVirtualPockets = ResourceUtil.loadResourcePathToMap(manager, "pockets/virtual", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(this::loadVirtualPocket), ResourceUtil.PATH_KEY_PROVIDER);
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

	public NbtElement getDataNbt(String id) {
		return this.dataTree.get(Path.stringPath(id));
	}

	public NbtCompound getDataNbtCompound(String id) {
		return NbtUtil.asNbtCompound(getDataNbt(id), "Could not convert NbtElement \"" + id + "\" to NbtCompound!");
	}

	private VirtualPocket loadVirtualPocket(NbtElement nbt, Path<String> ignore) {
		return VirtualPocket.deserialize(nbt);
	}

	private PocketGenerator loadPocketGenerator(NbtElement nbt, Path<String> ignore) {
		return PocketGenerator.deserialize(NbtUtil.asNbtCompound(nbt, "Could not load PocketGenerator since its json does not represent an NbtCompound!"));
	}

	private PocketTemplate loadPocketTemplate(NbtCompound nbt, Path<String> id) {
		try {
			return new PocketTemplate(Schematic.fromNbt(nbt), new Identifier(id.reduce(String::concat).orElseThrow()));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + nbt.toString(), e);
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

	public SimpleTree<String, VirtualPocket> getVirtualPockets() {
		return this.virtualPockets;
	}

	public PocketGenerator getGenerator(Identifier id) {
		return pocketGenerators.get(Path.stringPath(id));
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("dimdoors", "schematics_v2");
	}
}
