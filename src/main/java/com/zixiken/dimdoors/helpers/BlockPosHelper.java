package com.zixiken.dimdoors.helpers;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

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

    public static boolean lessThanOrEqual(BlockPos a, BlockPos b) {
        return (a.getX() <= b.getX() && a.getY() <= b.getY() && a.getZ() <= b.getZ());
    }

    public static BlockPos posFromSingleValue(double value) {
        return new BlockPos(value, value, value);
    }

    public static BlockPos divide(BlockPos volume, double factor) {
        return new BlockPos(volume.getX() / factor, volume.getY() / factor, volume.getZ() / factor);
    }
}
