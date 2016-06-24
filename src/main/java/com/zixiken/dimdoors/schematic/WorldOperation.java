package com.zixiken.dimdoors.schematic;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldOperation {

	private String name;
	
	public WorldOperation(String name)
	{
		this.name = name;
	}

	protected boolean initialize(World world, BlockPos pos, BlockPos volume) {
		return true;
	}
	
	protected abstract boolean applyToBlock(World world, BlockPos pos);

	protected boolean finish() {
		return true;
	}
	
	public boolean apply(World world, BlockPos pos, BlockPos volume) {
		if (!initialize(world, pos, volume))
			return false;
		
		int cx, cy, cz;
		BlockPos limit = pos.add(volume);
		
		//The order of these loops is important. Don't change it! It's used to avoid calculating
		//indeces in some schematic operations. The proper order is YZX.
		for (cy = pos.getY(); cy < limit.getY(); cy++) {
			for (cz = pos.getZ(); cz < limit.getZ(); cz++) {
				for (cx = pos.getX(); cx < limit.getX(); cx++) {
					if (!applyToBlock(world, new BlockPos(cx, cy, cz)))
						return false;
				}
			}
		}
		
		return finish();
	}
	
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
