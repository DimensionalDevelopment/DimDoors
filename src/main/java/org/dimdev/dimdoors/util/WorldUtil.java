package org.dimdev.dimdoors.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;

public class WorldUtil {
    public static ServerWorld getWorld(RegistryKey<World> key) {
        return DimensionalDoorsInitializer.getWorld(key);
    }
}
