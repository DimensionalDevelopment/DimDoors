package org.dimdev.dimdoors.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }
}
