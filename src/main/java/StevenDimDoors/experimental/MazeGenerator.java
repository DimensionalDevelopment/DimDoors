package StevenDimDoors.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import StevenDimDoors.mod_pocketDim.Point3D;

public class MazeGenerator
{
	public static final int ROOT_WIDTH = 40;
	public static final int ROOT_LENGTH = 40;
	public static final int ROOT_HEIGHT = 20;
	private static final int MIN_HEIGHT = 4;
	private static final int MIN_SIDE = 3;
	private static final int SPLIT_COUNT = 9;
	
	private MazeGenerator() { }
	
	public static void generate(World world, int x, int y, int z, Random random)
	{
		SpatialNode root = partitionRooms(ROOT_WIDTH, ROOT_HEIGHT, ROOT_LENGTH, SPLIT_COUNT, random);
		// Collect all the leaf nodes by performing a tree traversal
		ArrayList<SpatialNode> rooms = new ArrayList<SpatialNode>(1 << SPLIT_COUNT);
		listRooms(root, rooms);
		removeRandomRooms(rooms, random);
		buildRooms(root, world, new Point3D(x - ROOT_WIDTH / 2, y - ROOT_HEIGHT - 1, z - ROOT_WIDTH / 2));
	}
	
	private static void listRooms(SpatialNode node, ArrayList<SpatialNode> rooms)
	{
		if (node.isLeaf())
		{
			rooms.add(node);
		}
		else
		{
			listRooms(node.leftChild(), rooms);
			listRooms(node.rightChild(), rooms);
		}
	}
		
	private static void removeRandomRooms(ArrayList<SpatialNode> rooms, Random random)
	{
		// Randomly remove a fraction of the rooms
		Collections.shuffle(rooms, random);
		int remaining = rooms.size() / 2;
		for (int k = rooms.size() - 1; k >= remaining; k--)
		{
			removeRoom(rooms.remove(k));
		}
	}
	
	private static void removeRoom(SpatialNode node)
	{
		// Remove a node and any of its ancestors that become leaf nodes
		SpatialNode parent;
		SpatialNode current;
		
		current = node;
		while (current != null && current.isLeaf())
		{
			parent = current.parent();
			current.remove();
			current = parent;
		}
	}
	
	private static SpatialNode partitionRooms(int width, int height, int length, int maxLevels, Random random)
	{
		SpatialNode root = new SpatialNode(width, height, length);
		splitByRandomX(root, maxLevels, random);
		return root;
	}
	
	private static void splitByRandomX(SpatialNode node, int levels, Random random)
	{
		if (node.width() >= 2 * MIN_SIDE)
		{
			node.splitByX(MathHelper.getRandomIntegerInRange(random,
					node.minCorner().getX() + MIN_SIDE, node.maxCorner().getX() - MIN_SIDE + 1));

			if (levels > 1)
			{
				splitByRandomZ(node.leftChild(), levels - 1, random);
				splitByRandomZ(node.rightChild(), levels - 1, random);
			}
		}
		else if (levels > 1)
		{
			splitByRandomZ(node, levels - 1, random);
		}
	}
	
	private static void splitByRandomZ(SpatialNode node, int levels, Random random)
	{
		if (node.length() >= 2 * MIN_SIDE)
		{
			node.splitByZ(MathHelper.getRandomIntegerInRange(random,
					node.minCorner().getZ() + MIN_SIDE, node.maxCorner().getZ() - MIN_SIDE + 1));

			if (levels > 1)
			{
				splitByRandomY(node.leftChild(), levels - 1, random);
				splitByRandomY(node.rightChild(), levels - 1, random);
			}
		}
		else if (levels > 1)
		{
			splitByRandomY(node, levels - 1, random);
		}
	}
	
	private static void splitByRandomY(SpatialNode node, int levels, Random random)
	{
		if (node.height() >= 2 * MIN_HEIGHT)
		{
			node.splitByY(MathHelper.getRandomIntegerInRange(random,
					node.minCorner().getY() + MIN_HEIGHT, node.maxCorner().getY() - MIN_HEIGHT + 1));

			if (levels > 1)
			{
				splitByRandomX(node.leftChild(), levels - 1, random);
				splitByRandomX(node.rightChild(), levels - 1, random);
			}
		}
		else if (levels > 1)
		{
			splitByRandomX(node, levels - 1, random);
		}
	}
	
	private static void buildRooms(SpatialNode node, World world, Point3D offset)
	{
		if (node.isLeaf())
		{
			buildBox(world, offset, node.minCorner(), node.maxCorner());
		}
		else
		{
			if (node.leftChild() != null)
				buildRooms(node.leftChild(), world, offset);
			if (node.rightChild() != null)
				buildRooms(node.rightChild(), world, offset);
		}
	}
	
	private static void buildBox(World world, Point3D offset, Point3D minCorner, Point3D maxCorner)
	{
		int minX = minCorner.getX() + offset.getX();
		int minY = minCorner.getY() + offset.getY();
		int minZ = minCorner.getZ() + offset.getZ();
		
		int maxX = maxCorner.getX() + offset.getX();
		int maxY = maxCorner.getY() + offset.getY();
		int maxZ = maxCorner.getZ() + offset.getZ();
		
		int x, y, z;
		int blockID = Block.stoneBrick.blockID;
		
		for (x = minX; x <= maxX; x++)
		{
			for (z = minZ; z <= maxZ; z++)
			{
				setBlockDirectly(world, x, minY, z, blockID, 0);
				setBlockDirectly(world, x, maxY, z, blockID, 0);
			}
		}
		for (x = minX; x <= maxX; x++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, x, y, minZ, blockID, 0);
				setBlockDirectly(world, x, y, maxZ, blockID, 0);
			}
		}
		for (z = minZ; z <= maxZ; z++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, minX, y, z, blockID, 0);
				setBlockDirectly(world, maxX, y, z, blockID, 0);
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
