package org.dimdev.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public final class TeleportUtil {
    public static void teleport(Entity entity, Location location) {
        teleport(entity, location, entity.yaw, entity.pitch);
    }

    public static void teleport(Entity entity, Location location, float yaw, float pitch) {
        teleport(entity, location.world, location.pos.getX() + .5, location.pos.getY(), location.pos.getZ() + .5, yaw, pitch);
    }

    public static void teleport(Entity entity, BlockPos pos, float yaw, float pitch) {
        teleport(entity, entity.getEntityWorld(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
    }

    public static void teleport(Entity entity, double x, double y, double z, float yaw, float pitch) {
        teleport(entity, entity.getEntityWorld(), x, y, z, yaw, pitch);
    }

    public static void teleport(Entity entity, WorldView world, double x, double y, double z, float yaw, float pitch) {
        FabricDimensions.teleport(entity, world.getDimension().getType());
        entity.setPos(x, y, z);
        entity.yaw = yaw;
        entity.pitch = pitch;
        entity.setOnFireFor(0); // Workaround for https://bugs.mojang.com/browse/MC-100097
    }
}
