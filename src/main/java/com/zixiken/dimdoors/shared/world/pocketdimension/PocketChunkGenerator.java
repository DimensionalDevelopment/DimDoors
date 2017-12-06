package com.zixiken.dimdoors.shared.world.pocketdimension;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketChunkGenerator implements IChunkGenerator {

    private World worldObj;

    //private CustomLimboPopulator spawner;

    public PocketChunkGenerator(World world, long seed /*CustomLimboPopulator spawner*/) {
        worldObj = world;

        //this.spawner = spawner;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer primer = new ChunkPrimer();
        Chunk chunk = new Chunk(worldObj, primer, x, z);

        if(!chunk.isTerrainPopulated()) {
            chunk.setTerrainPopulated(true);
            //spawner.registerChunkForPopulation(worldObj.provider.dimensionId, chunkX, chunkZ);
        }
        return chunk;
    }

    @Override
    public void populate(int x, int z) {

    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
