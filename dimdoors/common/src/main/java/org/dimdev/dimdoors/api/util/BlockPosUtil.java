package org.dimdev.dimdoors.api.util;

import net.minecraft.core.BlockPos;

import java.util.function.Function;

public final class BlockPosUtil {
    private BlockPosUtil() {}

    public static <T> T nearbyVertical(BlockPos pos, Function<BlockPos, T> lookup) {
        T result = lookup.apply(pos);
        if (result != null) return result;

        result = lookup.apply(pos.below());
        if (result != null) return result;

        return lookup.apply(pos.above());
    }
}