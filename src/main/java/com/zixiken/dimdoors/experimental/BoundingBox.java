package com.zixiken.dimdoors.experimental;

import com.zixiken.dimdoors.helpers.BlockPosHelper;
import net.minecraft.util.BlockPos;

public class BoundingBox {
	protected BlockPos minCorner;
	protected BlockPos maxCorner;

	public BoundingBox(int x, int y, int z, int width, int height, int length) {
		this.minCorner = new BlockPos(x, y, z);
		this.maxCorner = new BlockPos(x + width - 1, y + height - 1, z + length - 1);
	}
	
	public BoundingBox(BlockPos minCorner, BlockPos maxCorner) {
		this.minCorner = minCorner;
		this.maxCorner = maxCorner;
	}
	
	public BlockPos volume() {
		return maxCorner.subtract(minCorner).add(1,1,1);
	}
	
	public BlockPos minCorner() {
		return minCorner;
	}
	
	public BlockPos maxCorner() {
		return maxCorner;
	}
	
	public boolean contains(BlockPos pos) {
		return BlockPosHelper.between(pos, minCorner, maxCorner);
	}
	
	public boolean intersects(BoundingBox other) {
		// To be clear, having one box inside another counts as intersecting
		
		boolean xi = (this.minCorner.getX() <= other.minCorner.getX() && other.minCorner.getX() <= this.maxCorner.getX()) ||
			(other.minCorner.getX() <= this.minCorner.getX() && this.minCorner.getX() <= other.maxCorner.getX());
		
		boolean yi = (this.minCorner.getY() <= other.minCorner.getY() && other.minCorner.getY() <= this.maxCorner.getY()) ||
				(other.minCorner.getY() <= this.minCorner.getY() && this.minCorner.getY() <= other.maxCorner.getY());
		
		boolean zi = (this.minCorner.getZ() <= other.minCorner.getZ() && other.minCorner.getZ() <= this.maxCorner.getZ()) ||
				(other.minCorner.getZ() <= this.minCorner.getZ() && this.minCorner.getZ() <= other.maxCorner.getZ());
		
		return xi && yi && zi;
	}
}
