package com.zixiken.dimdoors.world;

import java.util.List;

import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.ticking.CustomLimboPopulator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class PocketGenerator extends ChunkProviderGenerate {
	private World worldObj;

	private CustomLimboPopulator spawner;
	
	public PocketGenerator(World par1World, long par2, boolean par4, CustomLimboPopulator spawner) {
		super(par1World, par2, par4, null);
		this.worldObj = par1World;
		
		this.spawner = spawner;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return true;
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ) {
		Chunk chunk = new Chunk(worldObj, new ChunkPrimer(), chunkX, chunkZ);
		
		if(!chunk.isTerrainPopulated()) {
			chunk.setTerrainPopulated(true);
			spawner.registerChunkForPopulation(worldObj.provider.getDimensionId(), chunkX, chunkZ);
		}
		return chunk;
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
		DimData dimension = PocketManager.createDimensionData(this.worldObj);
		if (dimension != null && dimension.dungeon() != null && !dimension.dungeon().isOpen()) {
			return this.worldObj.getBiomeGenForCoords(pos).getSpawnableList(type);
		}
		return null;
	}

}