package org.dimdev.dimdoors.world.pocketdimension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.dimdev.pocketlib.PocketWorldDimension;

public class PersonalPocketDimension extends PocketWorldDimension {
    public PersonalPocketDimension(World world, DimensionType dimensionType, float f) {
        super(world, dimensionType, f);
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
