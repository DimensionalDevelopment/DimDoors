package StevenDimDoors.experimental;

import StevenDimDoors.mod_pocketDim.Point3D;

public class DoorwayData
{
	public static final char X_AXIS = 'X';
	public static final char Y_AXIS = 'Y';
	public static final char Z_AXIS = 'Z';
	
	private Point3D minCorner;
	private Point3D maxCorner;
	private char axis;
	
	public DoorwayData(Point3D minCorner, Point3D maxCorner, char axis)
	{
		this.minCorner = minCorner;
		this.maxCorner = maxCorner;
		this.axis = axis;
	}
	
	public Point3D minCorner()
	{
		return minCorner;
	}
	
	public Point3D maxCorner()
	{
		return maxCorner;
	}
	
	public char axis()
	{
		return axis;
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
}
