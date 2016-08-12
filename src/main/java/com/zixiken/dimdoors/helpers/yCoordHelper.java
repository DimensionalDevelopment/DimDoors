package com.zixiken.dimdoors.helpers;

import com.zixiken.dimdoors.Point3D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class yCoordHelper 
{
	private static final int MAXIMUM_UNCOVERED_Y = 245;
	
	private yCoordHelper() { }
	
	public static int getFirstUncovered(World world, BlockPos pos) {
		return getFirstUncovered(world, pos, false);
	}
	
	public static int getFirstUncovered(World world, BlockPos pos, boolean fromTop) {
		Chunk chunk = world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);

		int localX = pos.getX() < 0 ? (pos.getX() % 16) + 16 : (pos.getX() % 16);
		int localZ = pos.getZ() < 0 ? (pos.getZ() % 16) + 16 : (pos.getZ() % 16);
		int height = MAXIMUM_UNCOVERED_Y;

		int y;
		
		if (!fromTop) {
			boolean covered = true;
			for (y = pos.getY(); y < height && covered; y++) {
				covered = isCoveredBlock(chunk, localX, y - 1, localZ) || isCoveredBlock(chunk, localX, y, localZ);
			}
		}
		else {
			boolean covered = false;
			for (y = MAXIMUM_UNCOVERED_Y; y > 1 && !covered; y--) {
				covered = isCoveredBlock(chunk, localX, y - 1, localZ);
			}

			if (!covered) y = 63;
			y++;
		}

		return y;
	}
	
	public static boolean isCoveredBlock(Chunk chunk, int localX, int y, int localZ) {
		Block block;
		Material material;
		
		if (y < 0)
			return false;
		
		block = chunk.getBlock(localX, y, localZ);
		if (block == null)
			return false;
		
		material = block.getMaterial();
		return (material.isLiquid() || !material.isReplaceable());
	}
	
	public static BlockPos findSafeCubeUp(World world, int x, int startY, int z) {
		// Search for a 3x2x3 box with solid opaque blocks (without tile entities)
		// or replaceable blocks underneath. The box must contain either air and
		// non-liquid replaceable blocks only.
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
		IBlockState state;
		boolean isSafe;
		Block block;

		// Initialize layers to a huge negative number so that we won't
		// consider an area as usable unless it gets reset to 0 first
		// when we find a foundation upon which to build.
		int layers = -1000000;

		// Check if a 3x3 layer of blocks is empty
		// Treat non-liquid replaceable blocks like air
		// If we find a layer that contains replaceable blocks or solid opaque blocks without
		// tile entities, then it can serve as the base where we'll place the player and door.
		for (y = Math.max(startY - 1, 0); y < height; y++) {
			isSafe = true;
			for (dx = -1; dx <= 1 && isSafe; dx++) {
				for (dz = -1; dz <= 1 && isSafe; dz++) {
					block = chunk.getBlock(localX  + dx, y, localZ + dz);
					state = chunk.getBlockState(new BlockPos(localX  + dx, y, localZ + dz));
					if (!block.isAir(world, new BlockPos(x, y, z)) && (!block.getMaterial().isReplaceable() || block.getMaterial().isLiquid())) {
						if (!block.getMaterial().isReplaceable() && (!block.isOpaqueCube() || block.hasTileEntity(state))) {
							isSafe = false;
						}
						layers = 0;
					}
				}
			} if (isSafe) {
				layers++;
				if (layers == 3) {
					return new BlockPos(localX + cornerX, y - 2, localZ + cornerZ);
				}
			}
		}
		return null;
	}

	public static BlockPos findSafeCubeDown(World world, int x, int startY, int z) {
		// Search for a 3x2x3 box with solid opaque blocks (without tile entities)
		// or replaceable blocks underneath. The box must contain either air and
		// non-liquid replaceable blocks only.
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
		IBlockState state;
		boolean isSafe;
		boolean hasBlocks;
		Block block;
		int layers = 0;

		// Check if a 3x3 layer of blocks is empty
		// Treat non-liquid replaceable blocks like air
		// If we find a layer that contains replaceable blocks or solid opaque blocks without
		// tile entities, then it can serve as the base where we'll place the player and door.
		for (y = Math.min(startY + 2, height - 1); y >= 0; y--)
		{
			isSafe = true;
			hasBlocks = false;
			for (dx = -1; dx <= 1 && isSafe; dx++) {
				for (dz = -1; dz <= 1 && isSafe; dz++) {
					block = chunk.getBlock(localX  + dx, y, localZ + dz);
					state = chunk.getBlockState(new BlockPos(localX  + dx, y, localZ + dz));
					if (!block.isAir(world, new BlockPos(x,y,z)) && (!block.getMaterial().isReplaceable() || block.getMaterial().isLiquid())) {
						if (!block.getMaterial().isReplaceable() && (!block.isOpaqueCube() || block.hasTileEntity(state))) {
							if (layers >= 3) {
								return new BlockPos(localX + cornerX, y + 1, localZ + cornerZ);
							}
							isSafe = false;
						}
						hasBlocks = true;
					}
				}
			} if (isSafe) {
				layers++;
				if (hasBlocks) {
					if (layers >= 3) {
						return new BlockPos(localX + cornerX, y, localZ + cornerZ);
					}
					layers = 0;
				}
			}
		}
		return null;
	}
	
	private static Chunk initializeChunkArea(World world, int chunkX, int chunkZ) {
		// We initialize a 3x3 area of chunks instead of just initializing
		// the target chunk because things generated in adjacent chunks
		// (e.g. trees) might intrude into the target chunk.
		
		IChunkProvider provider = world.getChunkProvider();
		Chunk target = provider.provideChunk(chunkX, chunkZ);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				provider.provideChunk(chunkX, chunkZ);
			}
		}
		return target;
	}

	public static BlockPos findDropPoint(World world, int x, int startY, int z) {
		// Find a simple 2-block-high air gap
		// Search across a 3x3 column
		final int GAP_HEIGHT = 2;
		
		int localX = x < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = z < 0 ? (z % 16) + 16 : (z % 16);
		int cornerX = x - localX;
		int cornerZ = z - localZ;
		localX = MathHelper.clamp_int(localX, 1, 14);
		localZ = MathHelper.clamp_int(localZ, 1, 14);
		
		Chunk chunk = initializeChunkArea(world, x >> 4, z >> 4);
		
		int y, dx, dz, index;
		int height = world.getActualHeight();
		int[] gaps = new int[9];

		// Check 3x3 layers of blocks for air spaces
		for (y = Math.min(startY, height - 1); y > 0; y--) {
			for (dx = -1, index = 0; dx <= 1; dx++) {
				for (dz = -1; dz <= 1; dz++, index++) {
					if (!chunk.getBlock(localX  + dx, y, localZ + dz).isAir(world, new BlockPos(x+dx, y, z+dz))) {
						gaps[index] = 0;
					} else {
						gaps[index]++;
					}
				}
			}
			// Check if an acceptable gap exists in the center of the search column
			if (gaps[index / 2] == GAP_HEIGHT) {
				return new BlockPos(localX + cornerX, y + GAP_HEIGHT - 1, localZ + cornerZ);
			}
			// Check the other positions in the column
			for (dx = -1, index = 0; dx <= 1; dx++) {
				for (dz = -1; dz <= 1; dz++, index++) {
					if (gaps[index] == GAP_HEIGHT) {
						return new BlockPos(localX + cornerX + dx, y + GAP_HEIGHT - 1, localZ + cornerZ + dz);
					}
				}
			}
		}
		return null;
	}
	
	public static int adjustDestinationY(int y, int worldHeight, int entranceY, int dungeonHeight) {
		//The goal here is to guarantee that the dungeon fits within the vertical bounds
		//of the world while shifting it as little as possible.
		int destY = y;
		
		//Is the top of the dungeon going to be at Y < worldHeight?
		int pocketTop = (dungeonHeight - 1) + destY - entranceY;
		if (pocketTop >= worldHeight) {
			destY = (worldHeight - 1) - (dungeonHeight - 1) + entranceY;
		}
		
		//Is the bottom of the dungeon at Y >= 0?
		if (destY < entranceY) {
			destY = entranceY;
		}
		return destY;
	}
}
