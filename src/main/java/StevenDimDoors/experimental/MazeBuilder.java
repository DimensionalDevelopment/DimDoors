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
		Point3D offset = new Point3D(x - design.width() / 2, y - design.height() - 1, z - design.length() / 2);
		
		buildRooms(design.getRoomGraph(), world, offset);
		carveDoorways(design.getRoomGraph(), world, offset, random);
	}
	
	private static void buildRooms(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, Point3D offset)
	{
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			PartitionNode room = node.data();
			buildBox(world, offset, room.minCorner(), room.maxCorner(), Block.stoneBrick.blockID, 0);
		}
	}
	
	private static void carveDoorways(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, Point3D offset, Random random)
	{
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			for (IEdge<PartitionNode, DoorwayData> doorway : node.outbound())
			{
				char axis = doorway.data().axis();
				Point3D lower = doorway.data().minCorner();
				
				if (axis == DoorwayData.Z_AXIS)
				{
					carveDoorAlongZ(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ());
				}
				else if (axis == DoorwayData.X_AXIS)
				{
					carveDoorAlongX(world, offset.getX() + lower.getX(), offset.getY() + lower.getY() + 1,	offset.getZ() + lower.getZ() + 1);
				}
				else
				{
					carveHole(world, offset.getX() + lower.getX() + 1, offset.getY() + lower.getY(),	offset.getZ() + lower.getZ() + 1);
				}
			}
		}
	}
	
	private static void carveDoorAlongX(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, 0, 0);
		setBlockDirectly(world, x, y + 1, z, 0, 0);
		setBlockDirectly(world, x + 1, y, z, 0, 0);
		setBlockDirectly(world, x + 1, y + 1, z, 0, 0);
	}
	
	private static void carveDoorAlongZ(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, 0, 0);
		setBlockDirectly(world, x, y + 1, z, 0, 0);
		setBlockDirectly(world, x, y, z + 1, 0, 0);
		setBlockDirectly(world, x, y + 1, z + 1, 0, 0);
	}
	
	private static void carveHole(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, 0, 0);
		setBlockDirectly(world, x, y + 1, z, 0, 0);
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
