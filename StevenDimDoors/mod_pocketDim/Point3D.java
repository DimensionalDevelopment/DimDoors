package StevenDimDoors.mod_pocketDim;

import java.io.Serializable;

public class Point3D implements Serializable {

	private int x;
	private int y;
	private int z;

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
	
	public int setX(int x) 
	{
		return this.x = x;
	}

	public int setY(int y) 
	{
		return this.y = y;
	}
	
	public int setZ(int z) 
	{
		return this.z = z;
	}
	
	public Point3D clone()
	{
		return new Point3D(x, y, z);
	}
	
	public boolean equals(Point3D other) 
	{
		if (other == null)
			return false;
		
		if (this == other)
			return true;
		
		return (this.x == other.x && this.y == other.y && this.z == other.z);
	}
	
	public boolean equals(Object other)
	{
		return equals((Point3D) other);
	}
	
	@Override
	public int hashCode()
	{
		//Time for some witchcraft.
		//The code here is inspired by a discussion on Stack Overflow regarding hash codes for 3D.
		//Source: http://stackoverflow.com/questions/9858376/hashcode-for-3d-integer-coordinates-with-high-spatial-coherence
		
		//I believe that most of the time, any points we might be hashing will be in close proximity to each other.
		//For instance, points that are within the same chunk or within a few neighboring chunks. Only the low-order
		//bits of each component would differ. I'll use 8 bits from Y and the 12 bits from X and Z. ~SenseiKiwi
		
		int bit;
		int hash;
		int index;
		
		hash = 0;
		index = 0;
		for (bit = 0; bit < 8; bit++)
		{
			hash |= ((y >> bit) & 1) << index;
			index++;
			hash |= ((x >> bit) & 1) << index;
			index++;
			hash |= ((z >> bit) & 1) << index;
			index++;
		}
		for (; bit < 12; bit++)
		{
			hash |= ((x >> bit) & 1) << index;
			index++;
			hash |= ((z >> bit) & 1) << index;
			index++;
		}
		return hash;
	}
}