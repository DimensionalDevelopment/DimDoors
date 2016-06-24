package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IBlockSetter
{
	public void setBlock(World world, BlockPos pos, IBlockState state);
}
