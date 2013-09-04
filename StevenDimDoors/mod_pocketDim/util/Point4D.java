package StevenDimDoors.mod_pocketDim.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public final class Point4D implements Comparable<Point4D>
{
	private final int x;
	private final int y;
	private final int z;
	private final int dimension;
	
	public Point4D(int x, int y, int z, int dimension)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
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

	public int getDimension()
	{
		return dimension;
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
	
	public long toSeed()
	{
		//Time for some witchcraft.
		//The code here is inspired by a discussion on Stack Overflow regarding hash codes for 3D.
		//Source: http://stackoverflow.com/questions/9858376/hashcode-for-3d-integer-coordinates-with-high-spatial-coherence
		
		//Use 8 bits from Y and 16 bits from X and Z. Mix in 8 bits from the destination dim ID too - that means
		//even if you aligned two doors perfectly between two pockets, it's unlikely they would lead to the same dungeon.
		//We map bits in reverse order to produce more varied RNG output for nearly-identical points. The reason is
		//that Java's Random outputs the 32 MSBs of its internal state to produce its output. If the differences
		//between two seeds are small (i.e. in the LSBs), then they will tend to produce similar random outputs anyway!
		
		//Only bother to assign the 48 least-significant bits since Random only takes those bits from its seed.
		//NOTE: The casts to long are necessary to get the right results from the bit shifts!!!
		
		int bit;
		int index;
		long hash;
		final int w = this.dimension;
		final int x = this.x;
		final int y = this.y;
		final int z = this.z;
		
		hash = 0;
		index = 48;
		for (bit = 0; bit < 8; bit++)
		{
			hash |= (long) ((w >> bit) & 1) << index;
			index--;
			hash |= (long) ((x >> bit) & 1) << index;
			index--;
			hash |= (long) ((y >> bit) & 1) << index;
			index--;
			hash |= (long) ((z >> bit) & 1) << index;
			index--;
		}
		for (; bit < 16; bit++)
		{
			hash |= (long) ((x >> bit) & 1) << index;
			index--;
			hash |= (long) ((z >> bit) & 1) << index;
			index--;
		}
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals((Point4D) obj);
	}
	
	public boolean equals(Point4D other)
	{
		if (this == other)
			return true;
		if (other == null)
			return false;
		
		return (x == other.x && y == other.y && z == other.z && dimension == other.dimension);
	}

	@Override
	public int compareTo(Point4D other)
	{
		int diff = x - other.x;
		if (diff != 0)
			return diff;
		diff = y - other.y;
		if (diff != 0)
			return diff;
		diff = z - other.z;
		if (diff != 0)
			return diff;
		return dimension - other.dimension;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ", " + dimension + ")";
	}

	public static void write(Point4D point, DataOutputStream stream) throws IOException
	{
		stream.writeBoolean(point != null);
		if (point != null)
		{
			stream.writeInt(point.x);
			stream.writeInt(point.y);
			stream.writeInt(point.z);
			stream.writeInt(point.dimension);
		}
	}
	
	public static Point4D read(DataInputStream stream) throws IOException
	{
		if (stream.readBoolean())
		{
			return new Point4D( stream.readInt(), stream.readInt(), stream.readInt(), stream.readInt() );
		}
		else
		{
			return null;
		}
	}
}
