package org.dimdev.dimdoors.pockets;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PocketLoader implements ResourceManagerReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PocketLoader INSTANCE = new PocketLoader();
	private SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
	private SimpleTree<String, VirtualPocket> virtualPockets = new SimpleTree<>(String.class);
	private SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
	private SimpleTree<String, RiftData> dataTree = new SimpleTree<>(String.class);
	private SimpleTree<String, Modifier> modifiers = new SimpleTree<>(String.class);

	private PocketLoader() {
	}

	private BiFunction<InputStream, Path<String>, RiftData> RIFT_DATA_PROCESSOR = ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(RiftData.CODEC));
	private BiFunction<InputStream, Path<String>, Modifier> MODIFIER_PROCESSOR = ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(Modifier.CODEC));
	private BiFunction<InputStream, Path<String>, VirtualPocket> VIRTUAL_POCKET_PROCESSOR = ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(VirtualPocket.CODEC_BASE));
	private BiFunction<InputStream, Path<String>, PocketGenerator> POCKET_GENERATOR_PROCESSOR = ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(PocketGenerator.CODEC));

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

		CodecUtils.manager = manager;

		dataTree = ResourceUtil.loadResourcePathToMap(manager, "pockets/rift_data", ".json", new SimpleTree<>(String.class), ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(RiftData.CODEC_BASE)), ResourceUtil.PATH_KEY_PROVIDER).join();
		modifiers = ResourceUtil.loadResourcePathToMap(manager, "pockets/modifier", ".json", new SimpleTree<>(String.class), ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(Modifier.CODEC_BASE)), ResourceUtil.PATH_KEY_PROVIDER).join();
		pocketGenerators = ResourceUtil.loadResourcePathToMap(manager, "pockets/generators", ".json", new SimpleTree<>(String.class), ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(PocketGenerator.CODEC_BASE)), ResourceUtil.PATH_KEY_PROVIDER).join();
		pocketGroups = ResourceUtil.loadResourcePathToMap(manager, "pockets/groups", ".json", new SimpleTree<>(String.class), ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(VirtualPocket.CODEC_BASE)), ResourceUtil.PATH_KEY_PROVIDER).join();
		virtualPockets = ResourceUtil.loadResourcePathToMap(manager, "pockets/virtual", ".json", new SimpleTree<>(String.class), ResourceUtil.JSON_READER.andThenReader(jsonCodecLoader(VirtualPocket.CODEC_BASE)), ResourceUtil.PATH_KEY_PROVIDER).join();
		templates = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(this::loadPocketTemplate), ResourceUtil.PATH_KEY_PROVIDER).join();

		virtualPockets.values().forEach(VirtualPocket::init);
		pocketGroups.values().forEach(VirtualPocket::init);
		System.out.println();
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

	public RiftData getRiftData(Path<String> path) {
		return dataTree.get(path);
	}

	public RiftData getRiftData(String id) {
		return getRiftData(Path.stringPath(id));
	}

	public static <T> BiFunction<JsonElement, Path<String>, T> jsonCodecLoader(Codec<T> codec) {
		return (json, ignore) -> {
			try {
				var optional = codec.parse(JsonOps.INSTANCE, json).resultOrPartial();
				return optional.get();
			} catch (Exception e) {
				throw e;
			}
		};
	}

	private PocketTemplate loadPocketTemplate(CompoundTag nbt, Path<String> id) {
		try {
			return new PocketTemplate(Schematic.fromNbt(nbt), ResourceLocation.parse(id.reduce(String::concat).orElseThrow()));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + id.toString(), e);
		}
	}

	public WeightedList<PocketGenerator, PocketGenerationContext> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
		return new WeightedList<>(pocketGenerators.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(ResourceLocation group) {
		return getGroup(Path.stringPath(group));
	}

	public VirtualPocket getGroup(Path<String> path) {
		return pocketGroups.get(path);
	}

	public VirtualPocket getVirtual(Path<String> path) {
		return virtualPockets.get(path);
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

	public PocketGenerator getGenerator(Path<String> path) {
		return pocketGenerators.get(path);
	}

	public PocketGenerator getGenerator(ResourceLocation id) {
		return pocketGenerators.get(Path.stringPath(id));
	}

	public Modifier getModifier(Path<String> path) {
		return modifiers.get(path);
	}

}
