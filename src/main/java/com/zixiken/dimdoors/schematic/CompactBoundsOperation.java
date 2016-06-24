package com.zixiken.dimdoors.schematic;

import com.zixiken.dimdoors.helpers.BlockPosHelper;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CompactBoundsOperation extends WorldOperation {
	private BlockPos minCorner;
	private BlockPos maxCorner;
	
	public CompactBoundsOperation()
	{
		super("CompactBoundsOperation");
	}
	
	@Override
	protected boolean initialize(World world, BlockPos pos, BlockPos volume) {
		minCorner = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		maxCorner = pos;
		return true;
	}

	@Override
	protected boolean applyToBlock(World world, BlockPos pos) {
		//This could be done more efficiently, but honestly, this is the simplest approach and it
		//makes it easy for us to verify that the code is correct.
		if (!world.isAirBlock(pos)) {
			maxCorner = BlockPosHelper.greaterThan(pos, maxCorner) ? pos : maxCorner;
			minCorner = BlockPosHelper.lessThan(pos, minCorner) ? pos : minCorner;
		}
		return true;
	}
	
	@Override
	protected boolean finish() {
		if (minCorner.getX() == Integer.MAX_VALUE) {
			//The whole search space was empty!
			//Compact the space to a single block.
			minCorner = maxCorner;
			return false;
		}
		return true;
	}
	
	public BlockPos getMaxCorner() {
		return maxCorner;
	}
	
	public BlockPos getMinCorner() {
		return minCorner;
	}
}
