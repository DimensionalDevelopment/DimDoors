package com.zixiken.dimdoors.helpers;

import net.minecraft.util.BlockPos;

/**
 * Created by Jared Johnson on 6/22/2016.
 */
public class BlockPosHelper {
    public static boolean between(BlockPos pos, BlockPos min, BlockPos max) {
        return ((min.getX() <= pos.getX() && pos.getX() <= max.getX()) &&
                (min.getY() <= pos.getY() && pos.getY() <= max.getY()) &&
                (min.getZ() <= pos.getZ() && pos.getZ() <= max.getZ()));
    }

    public static BlockPos max(BlockPos a, BlockPos b) {
        return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
    }

    public static BlockPos min(BlockPos a, BlockPos b) {
        return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
    }

    public static boolean greaterThan(BlockPos a, BlockPos b) {
        return (a.getX() > b.getX() && a.getY() > b.getY() && a.getZ() > b.getZ());
    }

    public static boolean lessThan(BlockPos a, BlockPos b) {
        return (a.getX() < b.getX() && a.getY() < b.getY() && a.getZ() < b.getZ());
    }
}
