package org.dimdev.dimdoors.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
