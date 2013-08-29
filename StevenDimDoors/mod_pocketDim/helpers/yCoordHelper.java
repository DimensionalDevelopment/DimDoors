package StevenDimDoors.mod_pocketDim.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class yCoordHelper 
{
	private static final int MAXIMUM_UNCOVERED_Y = 245;
	
	private yCoordHelper() { }
	
	public static int getFirstUncovered(World world, int x, int yStart, int z)
	{
		return getFirstUncovered(world, x, yStart, z, false);
	}
	
	public static int getFirstUncovered(World world, int x, int yStart, int z, boolean fromTop)
	{
		Chunk chunk = world.getChunkProvider().loadChunk(x >> 4, z >> 4);

		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int height = MAXIMUM_UNCOVERED_Y;  //world.getHeight();
		int y;
		
		if (!fromTop)
		{
			boolean covered = true;
			for (y = yStart; y < height && covered; y++)
			{
				covered = isCoveredBlock(chunk, localX, y - 1, localZ) || isCoveredBlock(chunk, localX, y, localZ);
			}
		}
		else
		{
			boolean covered = false;
			for (y = MAXIMUM_UNCOVERED_Y; y > 1 && !covered; y--)
			{
				covered = isCoveredBlock(chunk, localX, y - 1, localZ);
			}
			if (!covered) y = 63;
			y++;
		}

		return y;
	}
	
	public static boolean isCoveredBlock(Chunk chunk, int localX, int y, int localZ)
	{
		int blockID;
		Block block;
		Material material;
		
		if (y < 0)
			return false;
		
		blockID = chunk.getBlockID(localX, y, localZ);
		if (blockID == 0)
			return false;

		block = Block.blocksList[blockID];
		if (block == null)
			return false;
		
		material = block.blockMaterial;
		return (material.isLiquid() || !material.isReplaceable());
	}

	public static int adjustDestinationY(int y, int worldHeight, int entranceY, int dungeonHeight)
	{
		//The goal here is to guarantee that the dungeon fits within the vertical bounds
		//of the world while shifting it as little as possible.
		int destY = y;
		
		//Is the top of the dungeon going to be at Y < worldHeight?
		int pocketTop = (dungeonHeight - 1) + destY - entranceY;
		if (pocketTop >= worldHeight)
		{
			destY = (worldHeight - 1) - (dungeonHeight - 1) + entranceY;
		}
		
		//Is the bottom of the dungeon at Y >= 0?
		if (destY < entranceY)
		{
			destY = entranceY;
		}
		return destY;
	}
}
