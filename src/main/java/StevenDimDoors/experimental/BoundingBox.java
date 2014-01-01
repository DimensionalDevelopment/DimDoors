package StevenDimDoors.experimental;

import StevenDimDoors.mod_pocketDim.Point3D;

public class BoundingBox
{
	protected Point3D minCorner;
	protected Point3D maxCorner;

	public BoundingBox(int x, int y, int z, int width, int height, int length)
	{
		this.minCorner = new Point3D(x, y, z);
		this.maxCorner = new Point3D(x + width - 1, y + height - 1, z + length - 1);
	}
	
	public BoundingBox(Point3D minCorner, Point3D maxCorner)
	{
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
	
	public Point3D minCorner()
	{
		return minCorner;
	}
	
	public Point3D maxCorner()
	{
		return maxCorner;
	}
	
	public boolean contains(int x, int y, int z)
	{
		return ((minCorner.getX() <= x && x <= maxCorner.getX()) &&
			(minCorner.getY() <= y && y <= maxCorner.getY()) &&
			(minCorner.getZ() <= z && z <= maxCorner.getZ()));
	}
	
	public boolean intersects(BoundingBox other)
	{
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
