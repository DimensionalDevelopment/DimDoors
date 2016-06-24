package com.zixiken.dimdoors.experimental;

import com.zixiken.dimdoors.Point3D;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

public class PartitionNode extends BoundingBox {
	private PartitionNode parent;
	private PartitionNode leftChild = null;
	private PartitionNode rightChild = null;
	
	public PartitionNode(BlockPos volume) {
		super(new BlockPos(0, 0, 0), volume.subtract(new Vec3i(1,1,1)));
		parent = null;
	}
	
	private PartitionNode(PartitionNode parent, BlockPos minCorner, BlockPos maxCorner)
	{
		super(minCorner, maxCorner);
		this.parent = parent;
	}
	
	public boolean isLeaf() {
		return (leftChild == null && rightChild == null);
	}
	
	public PartitionNode leftChild() {
		return leftChild;
	}
	
	public PartitionNode rightChild() {
		return rightChild;
	}
	
	public PartitionNode parent() {
		return parent;
	}
	
	public void splitByX(int rightStart) {
		if (!this.isLeaf()) {
			throw new IllegalStateException("This node has already been split.");
		} if (rightStart <= minCorner.getX() || rightStart > maxCorner.getX()) {
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new BlockPos(rightStart - 1, maxCorner.getY(), maxCorner.getZ()));
		rightChild = new PartitionNode(this, new BlockPos(rightStart, minCorner.getY(), minCorner.getZ()), maxCorner);
	}
	
	public void splitByY(int rightStart) {
		if (!this.isLeaf()) {
			throw new IllegalStateException("This node has already been split.");
		} if (rightStart <= minCorner.getY() || rightStart > maxCorner.getY()) {
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new BlockPos(maxCorner.getX(), rightStart - 1, maxCorner.getZ()));
		rightChild = new PartitionNode(this, new BlockPos(minCorner.getX(), rightStart, minCorner.getZ()), maxCorner);
	}
	
	public void splitByZ(int rightStart) {
		if (!this.isLeaf()) {
			throw new IllegalStateException("This node has already been split.");
		} if (rightStart <= minCorner.getZ() || rightStart > maxCorner.getZ())
		{
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new BlockPos(maxCorner.getX(), maxCorner.getY(), rightStart - 1));
		rightChild = new PartitionNode(this, new BlockPos(minCorner.getX(), minCorner.getY(), rightStart), maxCorner);
	}
	
	public void remove() {
		if (parent != null) {
			if (parent.leftChild == this)
				parent.leftChild = null;
			else
				parent.rightChild = null;
			parent = null;
		}
	}

	public PartitionNode findPoint(BlockPos pos) {
		// Find the lowest node that contains the specified point or return null
		if (this.contains(pos)) {
			return this.findPointInternal(pos);
		} else {
			return null;
		}
	}
	
	private PartitionNode findPointInternal(BlockPos pos) {
		if (leftChild != null && leftChild.contains(pos)) {
			return leftChild.findPointInternal(pos);
		} else if (rightChild != null && rightChild.contains(pos)) {
			return rightChild.findPointInternal(pos);
		} else {
			return this;
		}
	}
}
