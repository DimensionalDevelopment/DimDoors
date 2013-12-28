package StevenDimDoors.experimental;

import StevenDimDoors.mod_pocketDim.Point3D;

public class DoorwayData
{
	public final char X_AXIS = 'X';
	public final char Y_AXIS = 'Y';
	public final char Z_AXIS = 'Z';
	
	private RoomNode head;
	private RoomNode tail;
	private Point3D minCorner;
	private Point3D maxCorner;
	private char axis;
	
	public DoorwayData(RoomNode head, RoomNode tail, Point3D minCorner, Point3D maxCorner, char axis)
	{
		this.head = head;
		this.tail = tail;
		this.minCorner = minCorner;
		this.maxCorner = maxCorner;
		this.axis = axis;
	}
	
	public RoomNode head()
	{
		return head;
	}
	
	public RoomNode tail()
	{
		return tail;
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
}
