package org.dimdev.dimdoors.pockets;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.ResourceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PocketLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, PocketTemplate> templates = new HashMap<ResourceLocation, PocketTemplate>();

    public static void dump() {
    }

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
        templates.clear();
        load(manager, "pockets/schematic", ".schem", PocketTemplate.SchematicTemplate::create);
        load(manager, "pockets/nbt", ".nbt", PocketTemplate.NbtTemplate::create);
    }

    private static void load(ResourceManager manager, String directory, String extension, Function<CompoundTag, PocketTemplate> create) {
        ResourceUtil.loadResources(manager, directory, extension, (id, stream) -> create.apply(ResourceUtil.readCompressedNbt(stream))).forEach((id, template) -> templates.put(id.withSuffix(extension), template));
    }


    public static Map<ResourceLocation, PocketTemplate> getTemplates() {
        return templates;
    }
}