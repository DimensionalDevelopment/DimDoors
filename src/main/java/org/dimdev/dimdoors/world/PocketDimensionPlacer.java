package org.dimdev.dimdoors.world;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;

public class PocketDimensionPlacer implements EntityPlacer {
    @Override
    public BlockPattern.TeleportTarget placeEntity(Entity entity, ServerWorld serverWorld, Direction direction, double v, double v1) {
        return null;
    }
}
