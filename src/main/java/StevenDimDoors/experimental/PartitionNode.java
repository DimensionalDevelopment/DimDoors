package StevenDimDoors.experimental;

import StevenDimDoors.mod_pocketDim.Point3D;

public class PartitionNode
{
	private Point3D minCorner;
	private Point3D maxCorner;
	private PartitionNode parent;
	private PartitionNode leftChild = null;
	private PartitionNode rightChild = null;
	
	public PartitionNode(int width, int height, int length)
	{
		parent = null;
		minCorner = new Point3D(0, 0, 0);
		maxCorner = new Point3D(width - 1, height - 1, length - 1);
	}
	
	private PartitionNode(PartitionNode parent, Point3D minCorner, Point3D maxCorner)
	{
		this.parent = parent;
		this.minCorner = minCorner;
		this.maxCorner = maxCorner;
	}
	
	public int width()
	{
		return (maxCorner.getX() - minCorner.getX() + 1);
	}
	
	public int height()
	{
		return (maxCorner.getY() - minCorner.getY() + 1);
	}
	
	public int length()
	{
		return (maxCorner.getZ() - minCorner.getZ() + 1);
	}
	
	public boolean isLeaf()
	{
		return (leftChild == null && rightChild == null);
	}
	
	public PartitionNode leftChild()
	{
		return leftChild;
	}
	
	public PartitionNode rightChild()
	{
		return rightChild;
	}
	
	public Point3D minCorner()
	{
		return minCorner;
	}
	
	public Point3D maxCorner()
	{
		return maxCorner;
	}
	
	public PartitionNode parent()
	{
		return parent;
	}
	
	public void splitByX(int rightStart)
	{
		if (!this.isLeaf())
		{
			throw new IllegalStateException("This node has already been split.");
		}
		if (rightStart <= minCorner.getX() || rightStart > maxCorner.getX())
		{
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new Point3D(rightStart - 1, maxCorner.getY(), maxCorner.getZ()));
		rightChild = new PartitionNode(this, new Point3D(rightStart, minCorner.getY(), minCorner.getZ()), maxCorner);
	}
	
	public void splitByY(int rightStart)
	{
		if (!this.isLeaf())
		{
			throw new IllegalStateException("This node has already been split.");
		}
		if (rightStart <= minCorner.getY() || rightStart > maxCorner.getY())
		{
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new Point3D(maxCorner.getX(), rightStart - 1, maxCorner.getZ()));
		rightChild = new PartitionNode(this, new Point3D(minCorner.getX(), rightStart, minCorner.getZ()), maxCorner);
	}
	
	public void splitByZ(int rightStart)
	{
		if (!this.isLeaf())
		{
			throw new IllegalStateException("This node has already been split.");
		}
		if (rightStart <= minCorner.getZ() || rightStart > maxCorner.getZ())
		{
			throw new IllegalArgumentException("The specified cutting plane is invalid.");
		}
		leftChild = new PartitionNode(this, minCorner, new Point3D(maxCorner.getX(), maxCorner.getY(), rightStart - 1));
		rightChild = new PartitionNode(this, new Point3D(minCorner.getX(), minCorner.getY(), rightStart), maxCorner);
	}
	
	public void remove()
	{
		if (parent != null)
		{
			if (parent.leftChild == this)
				parent.leftChild = null;
			else
				parent.rightChild = null;
			parent = null;
		}
	}
}
