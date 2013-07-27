package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.world.World;

public abstract class WorldOperation {

	private String name;
	
	public WorldOperation(String name)
	{
		this.name = name;
	}

	protected boolean start(World world, int x, int y, int z, int width, int height, int length)
	{
		return true;
	}
	
	protected abstract boolean applyToBlock(World world, int x, int y, int z);

	protected boolean finish()
	{
		return true;
	}

	public boolean apply(World world, int x, int y, int z, int width, int height, int length)
	{
		if (!start(world, x, y, z, width, height, length))
			return false;
		
		int cx, cy, cz;
		int limitX = x + width;
		int limitY = y + height;
		int limitZ = z + length;
		
		//The order of these loops is important. Don't change it! It's used to avoid calculating
		//indeces in some schematic operations. The proper order is YZX.
		for (cy = y; cy < limitY; cy++)
		{
			for (cz = z; cz < limitZ; cz++)
			{
				for (cx = x; cx < limitX; cx++)
				{
					if (!applyToBlock(world, cx, cy, cz))
						return false;
				}
			}
		}
		
		return finish();
	}
	
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
