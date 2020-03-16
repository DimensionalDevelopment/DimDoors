package org.dimdev.dimdoors.world.limbo;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.dimdev.util.Location;


public class LimboDimension extends Dimension {
    public LimboDimension(World world, DimensionType dimensionType, float f) {
        super(world, dimensionType, f);
    }

    public static Location getLimboSkySpawn(Entity entity) {
        return null; // TODO
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        return null;
    }

    @Override
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
        return null;
    }

    @Override
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    public Vec3d modifyFogColor(Vec3d vec3d, float f) {
        return null;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean isFogThick(int i, int j) {
        return false;
    }

    @Override
    public DimensionType getType() {
        return null;
    }
}
