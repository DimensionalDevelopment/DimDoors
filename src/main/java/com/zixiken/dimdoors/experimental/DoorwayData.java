package com.zixiken.dimdoors.experimental;

import com.zixiken.dimdoors.Point3D;
import net.minecraft.util.BlockPos;

public class DoorwayData
{
	public static final char X_AXIS = 'X';
	public static final char Y_AXIS = 'Y';
	public static final char Z_AXIS = 'Z';
	
	private BlockPos minCorner;
	private BlockPos maxCorner;
	private char axis;
	
	public DoorwayData(BlockPos minCorner, BlockPos maxCorner, char axis)
	{
		this.minCorner = minCorner;
		this.maxCorner = maxCorner;
		this.axis = axis;
	}
	
	public BlockPos minCorner()
	{
		return minCorner;
	}
	
	public BlockPos maxCorner()
	{
		return maxCorner;
	}
	
	public char axis()
	{
		return axis;
	}
	
	public BlockPos volume() {
		return maxCorner.subtract(minCorner).add(1,1,1);
	}
}
