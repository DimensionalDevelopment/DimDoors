package org.dimdev.dimdoors.pockets;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PocketLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PocketLoader INSTANCE = new PocketLoader();
    private static SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
    private static SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
    private static SimpleTree<String, VirtualPocket> virtualPockets = new SimpleTree<>(String.class);
    private static SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
    private static SimpleTree<String, Tag> dataTree = new SimpleTree<>(String.class);

    public static void dump() {
    virtualPockets.forEach((path, pocketGenerator) -> LOGGER.info("Virtual Pocket: " + path + " -> " + pocketGenerator.toString()));
    pocketGroups.forEach((path, pocketGenerator) -> LOGGER.info("Pocket Group: " + path + " -> " + pocketGenerator.toString()));
        pocketGenerators.forEach((path, pocketGenerator) -> LOGGER.info("Pocket Generator: " + path + " -> " + pocketGenerator.toString()));
    }

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
    pocketGenerators.clear();
    pocketGroups.clear();
    virtualPockets.clear();
    templates.clear();
    dataTree.clear();

    dataTree = ResourceUtil.loadResourcePathToMap(manager, "pockets/rift_data", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.composeIdentity(), ResourceUtil.PATH_KEY_PROVIDER).join();

    CompletableFuture<SimpleTree<String, PocketGenerator>> futurePocketGeneratorMap = ResourceUtil.loadResourcePathToMap(manager, "pockets/generators", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(pocketGeneratorLoader(manager, provider)), ResourceUtil.PATH_KEY_PROVIDER);
    CompletableFuture<SimpleTree<String, VirtualPocket>> futurePocketGroups = ResourceUtil.loadResourcePathToMap(manager, "pockets/groups", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(virtualPocketLoader(manager, provider)), ResourceUtil.PATH_KEY_PROVIDER);
    CompletableFuture<SimpleTree<String, VirtualPocket>> futureVirtualPockets = ResourceUtil.loadResourcePathToMap(manager, "pockets/virtual", ".json", new SimpleTree<>(String.class), ResourceUtil.NBT_READER.andThenReader(virtualPocketLoader(manager, provider)), ResourceUtil.PATH_KEY_PROVIDER);
    CompletableFuture<SimpleTree<String, PocketTemplate>> futureTemplates = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(PocketLoader::loadPocketTemplate), ResourceUtil.PATH_KEY_PROVIDER);

        CompletableFuture.allOf(futurePocketGeneratorMap, futurePocketGroups, futureVirtualPockets, futureTemplates).join();

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
//    try {
//        Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/generators").toURI());
//        loadJson(path, new String[0], this::loadPocketGenerator);
//        LOGGER.info("Loaded pockets in {} seconds", System.currentTimeMillis() - startTime);
//    } catch (URISyntaxException e) {
//        LOGGER.error(e);
//    }
//
//    startTime = System.currentTimeMillis();
//    try {
//        Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/groups").toURI());
//        loadJson(path, new String[0], this::loadPocketGroup);
//        LOGGER.info("Loaded pocket groups in {} seconds", System.currentTimeMillis() - startTime);
//    } catch (URISyntaxException e) {
//        LOGGER.error(e);
//    }
//    }

    public static Tag getDataNbt(String id) {
    return dataTree.get(Path.stringPath(id));
    }

    public static CompoundTag getDataNbtCompound(String id) {
    return NbtUtil.asNbtCompound(getDataNbt(id), "Could not convert Tag \"" + id + "\" to CompoundTag!");
    }

    private static BiFunction<Tag, Path<String>, VirtualPocket> virtualPocketLoader(ResourceManager manager, HolderLookup.Provider provider) {
    return (nbt, ignore) -> {
        return VirtualPocket.deserialize(nbt, provider, manager);
    };
    }

    private static BiFunction<Tag, Path<String>, PocketGenerator> pocketGeneratorLoader(ResourceManager manager, HolderLookup.Provider provider) {
        return (nbt, ignore) -> {
            return PocketGenerator.deserialize(NbtUtil.asNbtCompound(nbt, "Could not load PocketGenerator since its json does not represent an CompoundTag!"), provider, manager);
        };
    }

    private static PocketTemplate loadPocketTemplate(CompoundTag nbt, Path<String> id) {
        try {
            return new PocketTemplate(Schematic.fromNbt(nbt), ResourceLocation.parse(id.reduce(String::concat).orElseThrow()));
        } catch (Exception e) {
            throw new RuntimeException("Error loading " + id.toString(), e);
        }
    }

    public static WeightedList<PocketGenerator, PocketGenerationContext> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
    return new WeightedList<>(pocketGenerators.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
    }

    public static VirtualPocket getGroup(ResourceLocation group) {
    return pocketGroups.get(Path.stringPath(group));
    }

    public static VirtualPocket getVirtual(ResourceLocation id) {
    return virtualPockets.get(Path.stringPath(id));
    }


    public static PocketLoader getInstance() {
    return INSTANCE;
    }

    public static SimpleTree<String, PocketTemplate> getTemplates() {
    return templates;
    }

    public static SimpleTree<String, VirtualPocket> getPocketGroups() {
    return pocketGroups;
    }

    public static SimpleTree<String, VirtualPocket> getVirtualPockets() {
    return virtualPockets;
    }

    public static SimpleTree<String, PocketGenerator> getPocketGenerators() {
        return pocketGenerators;
    }

    public static PocketGenerator getGenerator(ResourceLocation id) {
    return pocketGenerators.get(Path.stringPath(id));
    }
}