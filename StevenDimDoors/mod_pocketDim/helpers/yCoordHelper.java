package StevenDimDoors.mod_pocketDim.helpers;

import StevenDimDoors.mod_pocketDim.LinkData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;


public class yCoordHelper 
{
	private static final int MAXIMUM_UNCOVERED_Y = 245;
	
	public static int getFirstUncovered(LinkData pointerLink)
	{
		return yCoordHelper.getFirstUncovered(
				pointerLink.destDimID,
				pointerLink.destXCoord,
				pointerLink.destYCoord,
				pointerLink.destZCoord);
	}
	
	public static int getFirstUncovered(int worldID, int x, int yStart, int z)
	{
		if (dimHelper.getWorld(worldID) == null ||
			dimHelper.getWorld(worldID).provider == null)
	   	{
	   		dimHelper.initDimension(worldID);
	   	}
		
		return yCoordHelper.getFirstUncovered(dimHelper.getWorld(worldID), x, yStart, z);
	}
	
	public static int getFirstUncovered(World world, int x, int yStart, int z)
	{
		Chunk chunk = world.getChunkProvider().loadChunk(x >> 4, z >> 4);

		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int height = MAXIMUM_UNCOVERED_Y;  //world.getHeight();
		int y;
		
		boolean covered = true;
		for (y = yStart; y < height && covered; y++)
		{
			covered = IsCoveredBlock(chunk, localX, y - 1, localZ) || IsCoveredBlock(chunk, localX, y, localZ);
		}

		return y;
	}
	
	public static boolean IsCoveredBlock(Chunk chunk, int localX, int y, int localZ)
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
		return (!material.isLiquid() && !material.isReplaceable());
	}
}