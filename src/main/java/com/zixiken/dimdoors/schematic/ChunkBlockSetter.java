package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkBlockSetter implements IBlockSetter {
	private boolean ignoreAir;
	
	public ChunkBlockSetter(boolean ignoreAir) {
		this.ignoreAir = ignoreAir;
	}
	
	public void setBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock().isAir(world, pos) && ignoreAir) {
			return;
		}

		int cX = pos.getX() >> 4;
		int cZ = pos.getY() >> 4;

		Chunk chunk;

		int localX = (pos.getX() % 16) < 0 ? (pos.getX() % 16) + 16 : (pos.getX() % 16);
		int localZ = (pos.getZ() % 16) < 0 ? (pos.getZ() % 16) + 16 : (pos.getZ() % 16);

		try {
			chunk = world.getChunkFromChunkCoords(cX, cZ);
			chunk.setBlockState(new BlockPos(localX, pos.getY() & 15, localZ), state);
			chunk.setChunkModified();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
