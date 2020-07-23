package org.dimdev.util;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public final class TeleportUtil {
    public static void teleport(Entity entity, World world, BlockPos pos, int yawOffset) {
        teleport(entity, world, Vec3d.ofBottomCenter(pos), yawOffset);
    }

    public static void teleport(Entity entity, World world, Vec3d pos, float yawOffset) {
        if (entity.world.getRegistryKey().equals(world.getRegistryKey())) {
            entity.setPos(pos.x, pos.y, pos.z);
            entity.setYaw(entity.yaw + yawOffset);
        } else {
            if (world instanceof ServerWorld) {
                FabricDimensions.teleport(
                        entity,
                        (ServerWorld) world,
                        (e, serverWorld, direction, v, v1) -> new BlockPattern.TeleportTarget(pos, e.getVelocity(), (int) (e.yaw + yawOffset))
                );
                entity.setOnFireFor(0);// Workaround for https://bugs.mojang.com/browse/MC-100097
            }
        }
    }

    public static void teleport(ServerPlayerEntity player, Location location) {
        teleport(player, location.world, location.pos, 0);
    }

    public static void teleport(ServerPlayerEntity player, RotatedLocation location) {
        teleport(player, location.world, location.pos, (int) location.yaw);
    }
}
