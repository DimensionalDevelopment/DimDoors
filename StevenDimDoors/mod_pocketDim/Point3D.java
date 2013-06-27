package StevenDimDoors.mod_pocketDim;

import java.io.Serializable;

public class Point3D implements Serializable {

	private  int x;
	private  int y;
	private  int z;

	public Point3D(int x, int y,int z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() 
	{
		return x;
	}

	public int getY() 
	{
		return y;
	}

	public int getZ() 
	{
		return z;
	}

	public int setY(int y) 
	{
		return this.y=y;
	}
	
	public int setX(int x) 
	{
		return this. x=x;
	}
	
	public int setZ(int z) 
	{
		return this. z=z;
	}
	
	public Point3D clone()
	{
		return new Point3D(x, y, z);
	}
	
	public boolean equals(Object other) 
	{
		boolean result = false;
		if (other instanceof Point3D) 
		{
			Point3D that = (Point3D) other;
			result = (this.getX() == that.getX() && this.getY() == that.getY()&& this.getY() == that.getZ());
		}
		return result;
	}
}