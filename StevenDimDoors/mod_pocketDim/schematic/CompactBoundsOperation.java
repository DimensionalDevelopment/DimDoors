package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.Point3D;

public class CompactBoundsOperation extends WorldOperation
{
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;
	
	public CompactBoundsOperation()
	{
		super("CompactBoundsOperation");
	}
	
	@Override
	protected boolean initialize(World world, int x, int y, int z, int width, int height, int length)
	{
		minX = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		minZ = Integer.MAX_VALUE;
		maxX = x;
		maxY = y;
		maxZ = z;
		return true;
	}

	@Override
	protected boolean applyToBlock(World world, int x, int y, int z)
	{
		//This could be done more efficiently, but honestly, this is the simplest approach and it
		//makes it easy for us to verify that the code is correct.
		if (!world.isAirBlock(x, y, z))
		{
			maxX = x > maxX ? x : maxX;
			maxZ = z > maxZ ? z : maxZ;
			maxY = y > maxY ? y : maxY;

			minX = x < minX ? x : minX;
			minZ = z < minZ ? z : minZ;
			minY = y < minY ? y : minY;
		}
		return true;
	}
	
	protected boolean finish()
	{
		if (minX == Integer.MAX_VALUE)
		{
			//The whole search space was empty!
			//Compact the space to a single block.
			minX = maxX;
			minY = maxY;
			minZ = maxZ;
			return false;
		}
		return true;
	}
	
	public Point3D getMaxCorner()
	{
		return new Point3D(maxX, maxY, maxZ);
	}
	
	public Point3D getMinCorner()
	{
		return new Point3D(minX, minY, minZ);
	}
}
