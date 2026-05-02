package org.dimdev.dimdoors.world.structure.gateway;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

public interface Gateway {
    void generate(WorldGenLevel world, BlockPos pos);

    default boolean isLocationValid(WorldGenLevel world, BlockPos pos) {
        return true;
    }
}
