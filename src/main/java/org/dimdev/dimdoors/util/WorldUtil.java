package org.dimdev.dimdoors.util;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class WorldUtil {
    public static ServerWorld getWorld(RegistryKey<World> key) {
        return DimensionalDoorsInitializer.getWorld(key);
    }
}
