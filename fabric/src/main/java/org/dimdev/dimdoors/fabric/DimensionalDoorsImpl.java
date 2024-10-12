package org.dimdev.dimdoors.fabric;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static org.dimdev.dimdoors.DimensionalDoors.MOD_ID;
import static org.dimdev.dimdoors.DimensionalDoors.id;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void initBuiltinPacks() {
        ResourceManagerHelper.registerBuiltinResourcePack(id("default"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.ALWAYS_ENABLED);
        ResourceManagerHelper.registerBuiltinResourcePack(id("classic"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.NORMAL);
    }
}
