package org.dimdev.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public final class TeleportUtil {
    public static void teleport(Entity entity, World world, BlockPos pos, int yawOffset) {
        teleport(entity, world, Vec3d.method_24955(pos), yawOffset);
    }

    public static void teleport(Entity entity, World world, Vec3d pos, float yawOffset) {
        teleport(entity, world.dimension.getType(), pos, yawOffset);
    }

    public static void teleport(Entity entity, DimensionType dimension, Vec3d pos, float yawOffset) {
        if (entity.dimension == dimension) {
            entity.setPos(pos.x, pos.y, pos.z);
            entity.setYaw(entity.yaw + yawOffset);
        } else {
            FabricDimensions.teleport(
                    entity,
                    dimension,
                    (e, serverWorld, direction, v, v1) -> new BlockPattern.TeleportTarget(pos, e.getVelocity(), (int) (e.yaw + yawOffset))
            );

            entity.setOnFireFor(0); // Workaround for https://bugs.mojang.com/browse/MC-100097
        }
    }
}
