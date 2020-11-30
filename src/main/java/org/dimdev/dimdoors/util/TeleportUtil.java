package org.dimdev.dimdoors.util;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

@SuppressWarnings("deprecation")
public final class TeleportUtil {
    public static void teleport(Entity entity, World world, BlockPos pos, int yawOffset) {
        if (world.isClient) {
            throw new UnsupportedOperationException("Only supported on ServerWorld");
        }

        teleport(entity, world, Vec3d.ofBottomCenter(pos), yawOffset);
    }

    public static void teleport(Entity entity, World world, Vec3d pos, float yawOffset) {
        if (world.isClient) {
            throw new UnsupportedOperationException("Only supported on ServerWorld");
        }

        FabricDimensions.teleport(entity, (ServerWorld) world, new TeleportTarget(pos, Vec3d.ZERO, entity.getYaw(1.0F) + yawOffset, entity.getPitch(1.0F)));
    }

    public static void teleport(Entity entity, Location location) {
        teleport(entity, DimensionalDoorsInitializer.getWorld(location.world), location.pos, 0);
    }

    public static void teleport(Entity entity, RotatedLocation location) {
        teleport(entity, DimensionalDoorsInitializer.getWorld(location.world), location.pos, (int) location.yaw);
    }

    public static void teleportToLimbo(Entity entity) {
        teleport(entity, ModDimensions.LIMBO_DIMENSION, entity.getPos().multiply(1.0 / ModDimensions.LIMBO_DIMENSION.getDimension().getCoordinateScale()), 0);
    }
}
