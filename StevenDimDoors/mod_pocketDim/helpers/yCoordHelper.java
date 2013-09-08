package StevenDimDoors.mod_pocketDim.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import StevenDimDoors.mod_pocketDim.Point3D;

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
		int height = MAXIMUM_UNCOVERED_Y;
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
	
	public static Point3D findSafeCubeUp(World world, int x, int startY, int z)
	{
		// Search for a 3x3x3 cube of air with blocks underneath
		// We can also match against a 3x2x3 box with 
		// We shift the search area into the bounds of a chunk for the sake of simplicity,
		// so that we don't need to worry about working across chunks.
		
		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int cornerX = x - localX;
		int cornerZ = z - localZ;
		localX = MathHelper.clamp_int(localX, 1, 14);
		localZ = MathHelper.clamp_int(localZ, 1, 14);
		
		Chunk chunk = initializeChunkArea(world, x >> 4, z >> 4);
		
		int height = world.getActualHeight();
		int y, dx, dz, blockID;
		boolean isSafe;
		Block block;

		// Initialize layers to a huge negative number so that we won't
		// consider an area as usable unless it gets reset to 0 first
		// when we find a foundation upon which to build.
		int layers = -1000000;

		// Check if a 3x3 layer of blocks is empty
		// If we find a layer that contains replaceable blocks, it can
		// serve as the base where we'll place the player and door.
		for (y = Math.max(startY - 1, 0); y < height; y++)
		{
			isSafe = true;
			for (dx = -1; dx <= 1 && isSafe; dx++)
			{
				for (dz = -1; dz <= 1 && isSafe; dz++)
				{
					blockID = chunk.getBlockID(localX  + dx, y, localZ + dz);
					if (blockID != 0)
					{
						block = Block.blocksList[blockID];
						if (!block.blockMaterial.isReplaceable())
						{
							isSafe = false;
						}
						layers = 0;
					}
				}
			}
			if (isSafe)
			{
				layers++;
				if (layers == 3)
				{
					return new Point3D(localX + cornerX, y - 2, localZ + cornerZ);
				}
			}
		}
		return null;
	}

	public static Point3D findSafeCubeDown(World world, int x, int startY, int z)
	{
		// Search for a 3x3x3 cube of air with blocks underneath
		// We can also match against a 3x2x3 box with 
		// We shift the search area into the bounds of a chunk for the sake of simplicity,
		// so that we don't need to worry about working across chunks.
		
		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int cornerX = x - localX;
		int cornerZ = z - localZ;
		localX = MathHelper.clamp_int(localX, 1, 14);
		localZ = MathHelper.clamp_int(localZ, 1, 14);
		
		Chunk chunk = initializeChunkArea(world, x >> 4, z >> 4);
		
		int height = world.getActualHeight();
		int y, dx, dz, blockID;
		boolean isSafe;
		boolean hasBlocks;
		Block block;
		int layers = 0;

		// Check if a 3x3 layer of blocks is empty
		// If we find a layer that contains replaceable blocks, it can
		// serve as the base where we'll place the player and door.
		for (y = Math.min(startY + 2, height - 1); y >= 0; y--)
		{
			isSafe = true;
			hasBlocks = false;
			for (dx = -1; dx <= 1 && isSafe; dx++)
			{
				for (dz = -1; dz <= 1 && isSafe; dz++)
				{
					blockID = chunk.getBlockID(localX  + dx, y, localZ + dz);
					if (blockID != 0)
					{						
						block = Block.blocksList[blockID];
						if (!block.blockMaterial.isReplaceable())
						{
							if (layers >= 3)
							{
								return new Point3D(localX + cornerX, y + 1, localZ + cornerZ);
							}
							isSafe = false;
						}
						hasBlocks = true;
					}
				}
			}
			if (isSafe)
			{
				layers++;
				if (hasBlocks)
				{
					if (layers >= 3)
					{
						return new Point3D(localX + cornerX, y, localZ + cornerZ);
					}
					layers = 0;
				}
			}
		}
		return null;
	}
	
	private static Chunk initializeChunkArea(World world, int chunkX, int chunkZ)
	{
		// We initialize a 3x3 area of chunks instead of just initializing
		// the target chunk because things generated in adjacent chunks
		// (e.g. trees) might intrude into the target chunk.
		
		IChunkProvider provider = world.getChunkProvider();
		Chunk target = provider.loadChunk(chunkX, chunkZ);
		for (int dx = -1; dx <= 1; dx++)
		{
			for (int dz = -1; dz <= 1; dz++)
			{
				if (!provider.chunkExists(chunkX + dx, chunkZ + dz))
				{
					provider.loadChunk(chunkX, chunkZ);
				}
			}
		}
		return target;
	}

	public static Point3D findDropPoint(World world, int x, int y, int z)
	{
		/*// Find a simple 2-block-high air gap
		// Search across a 3x3 column
		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int cornerX = x - localX;
		int cornerZ = z - localZ;
		localX = MathHelper.clamp_int(localX, 1, 14);
		localZ = MathHelper.clamp_int(localZ, 1, 14);
		
		Chunk chunk = initializeChunkArea(world, x >> 4, z >> 4);
		
		int height = world.getActualHeight();
		int y, dx, dz, blockID;
		boolean isSafe;
		boolean hasBlocks;
		Block block;
		int layers = 0;

		// Check if a 3x3 layer of blocks is empty
		// If we find a layer that contains replaceable blocks, it can
		// serve as the base where we'll place the player and door.
		for (y = Math.min(startY + 2, height - 1); y >= 0; y--)
		{
			isSafe = true;
			hasBlocks = false;
			for (dx = -1; dx <= 1 && isSafe; dx++)
			{
				for (dz = -1; dz <= 1 && isSafe; dz++)
				{
					blockID = chunk.getBlockID(localX  + dx, y, localZ + dz);
					if (blockID != 0)
					{						
						block = Block.blocksList[blockID];
						if (!block.blockMaterial.isReplaceable())
						{
							if (layers >= 3)
							{
								return new Point3D(localX + cornerX, y + 1, localZ + cornerZ);
							}
							isSafe = false;
						}
						hasBlocks = true;
					}
				}
			}
			if (isSafe)
			{
				layers++;
				if (hasBlocks)
				{
					if (layers >= 3)
					{
						return new Point3D(localX + cornerX, y, localZ + cornerZ);
					}
					layers = 0;
				}
			}
		}
		return null;*/
		// Temporary measure to not break the build
		return new Point3D(x, y - 2, z);
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
