package com.zixiken.dimdoors.shared.world.pocket;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketGenerator implements IChunkGenerator {
    private World worldObj;

    //private CustomLimboPopulator spawner;

    public PocketGenerator(World world, long seed /*CustomLimboPopulator spawner*/) {
        this.worldObj = world;

        //this.spawner = spawner;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();
        Chunk chunk = new Chunk(worldObj, primer, chunkX, chunkZ);

        if(!chunk.isTerrainPopulated()) {
            chunk.setTerrainPopulated(true);
            //spawner.registerChunkForPopulation(worldObj.provider.dimensionId, chunkX, chunkZ);
        }
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {

    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return new ArrayList<Biome.SpawnListEntry>();
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }
}