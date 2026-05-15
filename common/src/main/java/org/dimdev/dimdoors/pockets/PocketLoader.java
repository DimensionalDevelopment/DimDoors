package org.dimdev.dimdoors.pockets;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.schematic.Schematic;

public class PocketLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static SimpleTree<String, PocketGenerator> pocketGenerators = new SimpleTree<>(String.class);
    private static SimpleTree<String, VirtualPocket> pocketGroups = new SimpleTree<>(String.class);
    private static SimpleTree<String, VirtualPocket> virtualPockets = new SimpleTree<>(String.class);
    private static SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
    private static SimpleTree<String, Tag> dataTree = new SimpleTree<>(String.class);

    public static void dump() {
    }

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
        templates.clear();
        templates = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(PocketLoader::loadPocketTemplate), ResourceUtil.PATH_KEY_PROVIDER);
    }

    private static PocketTemplate loadPocketTemplate(CompoundTag nbt, Path<String> id) {
        try {
            return new PocketTemplate(Schematic.fromNbt(nbt), ResourceLocation.parse(id.reduce(String::concat).orElseThrow()));
        } catch (Exception e) {
            throw new RuntimeException("Error loading " + id.toString(), e);
        }
    }

    public static SimpleTree<String, PocketTemplate> getTemplates() {
        return templates;
    }

}