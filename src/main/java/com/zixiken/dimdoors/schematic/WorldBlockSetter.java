package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldBlockSetter implements IBlockSetter {
	private boolean ignoreAir;
	
	public WorldBlockSetter(boolean doBlockUpdates, boolean notifyClients, boolean ignoreAir) {
		this.ignoreAir = ignoreAir;
	}

	public void setBlock(World world, BlockPos pos, IBlockState state) {
		if (!ignoreAir || !world.isAirBlock(pos)) {
			world.setBlockState(pos, state);
		}
	}
}
