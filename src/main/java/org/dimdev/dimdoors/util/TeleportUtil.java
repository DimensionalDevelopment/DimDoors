package org.dimdev.dimdoors.util;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;


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
                entity.moveToWorld((ServerWorld) world);
                entity.setPos(pos.x, pos.y, pos.z);
                entity.setYaw(entity.yaw + yawOffset);
                entity.setOnFireFor(0);// Workaround for https://bugs.mojang.com/browse/MC-100097
            }
        }
    }

    public static void teleport(ServerPlayerEntity player, Location location) {
        teleport(player, WorldUtil.getWorld(location.world), location.pos, 0);
    }

    public static void teleport(ServerPlayerEntity player, RotatedLocation location) {
        teleport(player, WorldUtil.getWorld(location.world), location.pos, (int) location.yaw);
    }
}
