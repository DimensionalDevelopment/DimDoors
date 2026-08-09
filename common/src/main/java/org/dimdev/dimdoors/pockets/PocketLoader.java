package org.dimdev.dimdoors.pockets;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.util.schematic.Schematic;

public class PocketLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);

    public static void dump() {
    }

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
        templates.clear();
        var schematics = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(PocketLoader::loadSchematicTemplate), ResourceUtil.PATH_KEY_PROVIDER_WITH_EXTENSION);
        var nbts = ResourceUtil.loadResourcePathToMap(manager, "pockets/nbt", ".nbt", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(PocketLoader::loadNbtTemplate), ResourceUtil.PATH_KEY_PROVIDER_WITH_EXTENSION);

        templates.putAll(schematics);
        templates.putAll(nbts);
    }

    private static PocketTemplate loadSchematicTemplate(CompoundTag nbt, Path<String> id) {
        try {
            return new PocketTemplate.SchematicTemplate(Schematic.fromNbt(nbt));
        } catch (Exception e) {
            throw new RuntimeException("Error loading " + id.toString(), e);
        }
    }

    private static PocketTemplate loadNbtTemplate(CompoundTag nbt, Path<String> id) {
        try {
            return new PocketTemplate.NbtTemplate(PocketTemplate.NbtTemplate.load(nbt));
        } catch (Exception e) {
            throw new RuntimeException("Error loading " + id.toString(), e);
        }
    }


    public static SimpleTree<String, PocketTemplate> getTemplates() {
        return templates;
    }

}