package StevenDimDoors.experimental;

import StevenDimDoors.mod_pocketDim.Point3D;

public class PartitionNode extends BoundingBox
{
	private PartitionNode parent;
	private PartitionNode leftChild = null;
	private PartitionNode rightChild = null;
	
	public PartitionNode(int width, int height, int length)
	{
		super(new Point3D(0, 0, 0), new Point3D(width - 1, height - 1, length - 1));
		parent = null;
	}
	
	private PartitionNode(PartitionNode parent, Point3D minCorner, Point3D maxCorner)
	{
		super(minCorner, maxCorner);
		this.parent = parent;
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

	public PartitionNode findPoint(int x, int y, int z)
	{
		// Find the lowest node that contains the specified point or return null
		if (this.contains(x, y, z))
		{
			return this.findPointInternal(x, y, z);
		}
		else
		{
			return null;
		}
	}
	
	private PartitionNode findPointInternal(int x, int y, int z)
	{
		if (leftChild != null && leftChild.contains(x, y, z))
		{
			return leftChild.findPointInternal(x, y, z);
		}
		else if (rightChild != null && rightChild.contains(x, y, z))
		{
			return rightChild.findPointInternal(x, y, z);
		}
		else
		{
			return this;
		}
	}
}
