package com.zixiken.dimdoors.tileentities;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class DDTileEntityBase extends TileEntity
{
	/**
	 * 
	 * @return an array of floats representing RGBA color where 1.0 = 255.
	 */
	public abstract float[] getRenderColor(Random rand);

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
}
