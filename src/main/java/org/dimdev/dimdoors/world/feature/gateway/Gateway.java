package org.dimdev.dimdoors.world.feature.gateway;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public interface Gateway {
    void generate(StructureWorldAccess world, BlockPos pos);

    default boolean isLocationValid(StructureWorldAccess world, BlockPos pos) {
        return true;
    }
}
