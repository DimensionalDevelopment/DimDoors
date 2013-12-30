package StevenDimDoors.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import StevenDimDoors.mod_pocketDim.Point3D;

public class MazeBuilder
{
	private MazeBuilder() { }
	
	public static void generate(World world, int x, int y, int z, Random random)
	{
		MazeDesign design = MazeDesigner.generate(random);
		
		buildRooms(design.getRoomGraph(), world,
				new Point3D(x - design.width() / 2, y - design.height() - 1, z - design.length() / 2));
	}
	
	private static void buildRooms(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, Point3D offset)
	{
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			PartitionNode room = node.data();
			buildBox(world, offset, room.minCorner(), room.maxCorner(), Block.stoneBrick.blockID, 0);
		}
		
		// TESTING!!!
		// This code carves out cheap doorways
		// The final system will be better
		// This has to happen after all the rooms have been built or the passages will be overwritten sometimes
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			for (IEdge<PartitionNode, DoorwayData> doorway : node.outbound())
			{
				char axis = doorway.data().axis();
				Point3D lower = doorway.data().minCorner();
				
				if (axis == DoorwayData.Z_AXIS)
				{
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ(), 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 2,	offset.getZ() + lower.getZ(), 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 2,	offset.getZ() + lower.getZ() + 1, 0, 0);
				}
				else if (axis == DoorwayData.X_AXIS)
				{
					setBlockDirectly(world, offset.getX() + lower.getX(), offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX(), offset.getY() + lower.getY() + 2,	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 2,	offset.getZ() + lower.getZ() + 1, 0, 0);
				}
				else
				{
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY(),	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY(),	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1, 0, 0);
					setBlockDirectly(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1, 0, 0);
				}
			}
		}
	}
	
	private static void buildBox(World world, Point3D offset, Point3D minCorner, Point3D maxCorner, int blockID, int metadata)
	{
		int minX = minCorner.getX() + offset.getX();
		int minY = minCorner.getY() + offset.getY();
		int minZ = minCorner.getZ() + offset.getZ();
		
		int maxX = maxCorner.getX() + offset.getX();
		int maxY = maxCorner.getY() + offset.getY();
		int maxZ = maxCorner.getZ() + offset.getZ();
		
		int x, y, z;
		
		for (x = minX; x <= maxX; x++)
		{
			for (z = minZ; z <= maxZ; z++)
			{
				setBlockDirectly(world, x, minY, z, blockID, metadata);
				setBlockDirectly(world, x, maxY, z, blockID, metadata);
			}
		}
		for (x = minX; x <= maxX; x++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, x, y, minZ, blockID, metadata);
				setBlockDirectly(world, x, y, maxZ, blockID, metadata);
			}
		}
		for (z = minZ; z <= maxZ; z++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, minX, y, z, blockID, metadata);
				setBlockDirectly(world, maxX, y, z, blockID, metadata);
			}
		}
	}
	
	private static void setBlockDirectly(World world, int x, int y, int z, int blockID, int metadata)
	{
		if (blockID != 0 && Block.blocksList[blockID] == null)
		{
			return;
		}

		int cX = x >> 4;
		int cZ = z >> 4;
		int cY = y >> 4;
		Chunk chunk;

		int localX = (x % 16) < 0 ? (x % 16) + 16 : (x % 16);
		int localZ = (z % 16) < 0 ? (z % 16) + 16 : (z % 16);
		ExtendedBlockStorage extBlockStorage;

		chunk = world.getChunkFromChunkCoords(cX, cZ);
		extBlockStorage = chunk.getBlockStorageArray()[cY];
		if (extBlockStorage == null) 
		{
			extBlockStorage = new ExtendedBlockStorage(cY << 4, !world.provider.hasNoSky);
			chunk.getBlockStorageArray()[cY] = extBlockStorage;
		}
		extBlockStorage.setExtBlockID(localX, y & 15, localZ, blockID);
		extBlockStorage.setExtBlockMetadata(localX, y & 15, localZ, metadata);
	}
}
