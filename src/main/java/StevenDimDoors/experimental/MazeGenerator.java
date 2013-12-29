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
		// Construct a random binary space partitioning of our maze volume
		PartitionNode root = partitionRooms(ROOT_WIDTH, ROOT_HEIGHT, ROOT_LENGTH, SPLIT_COUNT, random);

		// List all the leaf nodes of the partition tree, which denote individual rooms
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>(1 << SPLIT_COUNT);
		listRoomPartitions(root, partitions);
		
		// Construct an adjacency graph of the rooms we've carved out. Two rooms are
		// considered adjacent if and only if a doorway could connect them. Their
		// common boundary must be large enough for a doorway.
		DirectedGraph<PartitionNode, DoorwayData> rooms = createRoomGraph(root, partitions, random);
		
		// Cut out random subgraphs from the adjacency graph
		ArrayList<IGraphNode<PartitionNode, DoorwayData>> cores = createMazeSections(rooms, random);
		
		
		buildRooms(rooms, world, new Point3D(x - ROOT_WIDTH / 2, y - ROOT_HEIGHT - 1, z - ROOT_WIDTH / 2));
	}
	
	private static void listRoomPartitions(PartitionNode node, ArrayList<PartitionNode> partitions)
	{
		if (node.isLeaf())
		{
			partitions.add(node);
		}
		else
		{
			listRoomPartitions(node.leftChild(), partitions);
			listRoomPartitions(node.rightChild(), partitions);
		}
	}
	
	private static void removeRoom(PartitionNode node)
	{
		// Remove a node and any of its ancestors that become leaf nodes
		PartitionNode parent;
		PartitionNode current;
		
		current = node;
		while (current != null && current.isLeaf())
		{
			parent = current.parent();
			current.remove();
			current = parent;
		}
	}
	
	private static PartitionNode partitionRooms(int width, int height, int length, int maxLevels, Random random)
	{
		PartitionNode root = new PartitionNode(width, height, length);
		splitByRandomX(root, maxLevels, random);
		return root;
	}
	
	private static void splitByRandomX(PartitionNode node, int levels, Random random)
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
	
	private static void splitByRandomZ(PartitionNode node, int levels, Random random)
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
	
	private static void splitByRandomY(PartitionNode node, int levels, Random random)
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
	
	private static DirectedGraph<PartitionNode, DoorwayData> createRoomGraph(PartitionNode root, ArrayList<PartitionNode> partitions, Random random)
	{
		DirectedGraph<PartitionNode, DoorwayData> roomGraph = new DirectedGraph<PartitionNode, DoorwayData>();
		HashMap<PartitionNode, IGraphNode<PartitionNode, DoorwayData>> roomsToGraph = new HashMap<PartitionNode, IGraphNode<PartitionNode, DoorwayData>>(2 * partitions.size());
		
		// Shuffle the list of rooms so that they're not listed in any ordered way in the room graph
		// This is the only convenient way of randomizing the maze sections generated later
		Collections.shuffle(partitions, random);
		
		// Add all rooms to a graph
		// Also add them to a map so we can associate rooms with their graph nodes
		// The map is needed for linking graph nodes based on adjacent partitions
		for (PartitionNode partition : partitions)
		{
			roomsToGraph.put(partition, roomGraph.addNode(partition));
		}
		
		// Add edges for each room
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			findDoorways(node, root, roomsToGraph, roomGraph);
		}
		
		return roomGraph;
	}
	
	private static void findDoorways(IGraphNode<PartitionNode, DoorwayData> roomNode, PartitionNode root,
			HashMap<PartitionNode, IGraphNode<PartitionNode, DoorwayData>> roomsToGraph,
			DirectedGraph<PartitionNode, DoorwayData> roomGraph)
	{
		// This function finds rooms adjacent to a specified room that could be connected
		// to it through a doorway. Edges are added to the room graph to denote rooms that
		// could be connected. The areas of their common bounds that could be carved
		// out for a passage are stored in the edges.
		
		// Three directions have to be checked: up, forward, and right. The other three
		// directions (down, back, left) aren't checked because other nodes will cover them.
		// That is, down for this room is up for some other room, if it exists. Also, rooms
		// are guaranteed to have at least one doorway to another room, because the minimum
		// dimensions to which a room can be partitioned still allow passages along all
		// its sides. A room's sibling in the partition tree is guaranteed to share a side
		// through which a doorway could exist. Similar arguments guarantee the existence
		// of passages such that the whole set of rooms is a connected graph - in other words,
		// there will always be a way to walk from any room to any other room.
		
		boolean[][] detected;
		PartitionNode adjacent;
		
		int a, b, c;
		int p, q, r;
		int minXI, minYI, minZI;
		int maxXI, maxYI, maxZI;
		Point3D otherMin;
		Point3D otherMax;
		DoorwayData doorway;
		IGraphNode<PartitionNode, DoorwayData> adjacentNode;
		
		PartitionNode room = roomNode.data();
		Point3D minCorner = room.minCorner();
		Point3D maxCorner = room.maxCorner();
		
		int minX = minCorner.getX();
		int minY = minCorner.getY();
		int minZ = minCorner.getZ();
		
		int maxX = maxCorner.getX();
		int maxY = maxCorner.getY();
		int maxZ = maxCorner.getZ();
		
		int width = room.width();
		int height = room.height();
		int length = room.length();
		
		if (maxZ < root.maxCorner().getZ())
		{
			// Check for adjacent rooms along the XY plane
			detected = new boolean[width][height];
			for (a = 0; a < width; a++)
			{
				for (b = 0; b < height; b++)
				{
					if (!detected[a][b])
					{
						adjacent = root.findPoint(minX + a, minY + b, maxZ + 1);
						if (adjacent != null)
						{
							// Compute the dimensions available for a doorway
							otherMin = adjacent.minCorner();
							otherMax = adjacent.maxCorner();
							minXI = Math.max(minX, otherMin.getX());
							maxXI = Math.min(maxX, otherMax.getX());
							minYI = Math.max(minY, otherMin.getY());
							maxYI = Math.min(maxY, otherMax.getY());
							
							for (p = a; p <= maxXI - minXI; p++)
							{
								for (q = b; q <= maxYI - minYI; q++)
								{
									detected[p][q] = true;
								}	
							}
							// Check if we meet the minimum dimensions needed for a doorway
							if (maxXI - minXI + 1 >= MIN_SIDE && maxYI - minYI + 1 >= MIN_HEIGHT)
							{
								otherMin = new Point3D(minXI, minYI, maxZ);
								otherMax = new Point3D(maxXI, maxYI, maxZ + 1);
								doorway = new DoorwayData(otherMin, otherMax, DoorwayData.Z_AXIS);
								adjacentNode = roomsToGraph.get(adjacent);
								roomGraph.addEdge(roomNode, adjacentNode, doorway);
							}
						}
						else
						{
							detected[a][b] = true;
						}
					}
				}
			}
		}
		
		
		if (maxX < root.maxCorner().getX())
		{
			// Check for adjacent rooms along the YZ plane
			detected = new boolean[height][length];
			for (b = 0; b < height; b++)
			{
				for (c = 0; c < length; c++)
				{
					if (!detected[b][c])
					{
						adjacent = root.findPoint(maxX + 1, minY + b, minZ + c);
						if (adjacent != null)
						{
							// Compute the dimensions available for a doorway
							otherMin = adjacent.minCorner();
							otherMax = adjacent.maxCorner();
							minYI = Math.max(minY, otherMin.getY());
							maxYI = Math.min(maxY, otherMax.getY());
							minZI = Math.max(minZ, otherMin.getZ());
							maxZI = Math.min(maxZ, otherMax.getZ());
							
							for (q = b; q <= maxYI - minYI; q++)
							{
								for (r = c; r <= maxZI - minZI; r++)
								{
									detected[q][r] = true;
								}	
							}
							// Check if we meet the minimum dimensions needed for a doorway
							if (maxYI - minYI + 1 >= MIN_HEIGHT && maxZI - minZI + 1 >= MIN_SIDE)
							{
								otherMin = new Point3D(maxX, minYI, minZI);
								otherMax = new Point3D(maxX + 1, maxYI, maxZI);
								doorway = new DoorwayData(otherMin, otherMax, DoorwayData.X_AXIS);
								adjacentNode = roomsToGraph.get(adjacent);
								roomGraph.addEdge(roomNode, adjacentNode, doorway);
							}
						}
						else
						{
							detected[b][c] = true;
						}
					}
				}
			}
		}
		
		
		if (maxY < root.maxCorner().getY())
		{
			// Check for adjacent rooms along the XZ plane
			detected = new boolean[width][length];
			for (a = 0; a < width; a++)
			{
				for (c = 0; c < length; c++)
				{
					if (!detected[a][c])
					{
						adjacent = root.findPoint(minX + a, maxY + 1, minZ + c);
						if (adjacent != null)
						{
							// Compute the dimensions available for a doorway
							otherMin = adjacent.minCorner();
							otherMax = adjacent.maxCorner();
							minXI = Math.max(minX, otherMin.getX());
							maxXI = Math.min(maxX, otherMax.getX());
							minZI = Math.max(minZ, otherMin.getZ());
							maxZI = Math.min(maxZ, otherMax.getZ());
							
							for (p = a; p <= maxXI - minXI; p++)
							{
								for (r = c; r <= maxZI - minZI; r++)
								{
									detected[p][r] = true;
								}	
							}
							// Check if we meet the minimum dimensions needed for a doorway
							if (maxXI - minXI + 1 >= MIN_SIDE && maxZI - minZI + 1 >= MIN_SIDE)
							{
								otherMin = new Point3D(minXI, maxY, minZI);
								otherMax = new Point3D(maxXI, maxY + 1, maxZI);
								doorway = new DoorwayData(otherMin, otherMax, DoorwayData.Y_AXIS);
								adjacentNode = roomsToGraph.get(adjacent);
								roomGraph.addEdge(roomNode, adjacentNode, doorway);
							}
						}
						else
						{
							detected[a][c] = true;
						}
					}
				}
			}
		}
		
		//Done!
	}
	
	private static ArrayList<IGraphNode<PartitionNode, DoorwayData>> createMazeSections(DirectedGraph<PartitionNode, DoorwayData> roomGraph, Random random)
	{
		// The randomness of the sections generated here hinges on
		// the nodes in the graph being in a random order. We assume
		// that was handled in a previous step!
		
		final int MAX_DISTANCE = 2;
		final int MIN_SECTION_ROOMS = 5;
		
		int distance;
		IGraphNode<PartitionNode, DoorwayData> current;
		IGraphNode<PartitionNode, DoorwayData> neighbor;
		
		ArrayList<IGraphNode<PartitionNode, DoorwayData>> cores = new ArrayList<IGraphNode<PartitionNode, DoorwayData>>();
		ArrayList<IGraphNode<PartitionNode, DoorwayData>> removals = new ArrayList<IGraphNode<PartitionNode, DoorwayData>>();
		ArrayList<IGraphNode<PartitionNode, DoorwayData>> section = new ArrayList<IGraphNode<PartitionNode, DoorwayData>>();
		
		Queue<IGraphNode<PartitionNode, DoorwayData>> ordering = new LinkedList<IGraphNode<PartitionNode, DoorwayData>>();
		HashMap<IGraphNode<PartitionNode, DoorwayData>, Integer> distances = new HashMap<IGraphNode<PartitionNode, DoorwayData>, Integer>();
		
		// Repeatedly generate sections until all nodes have been visited
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			// If this node hasn't been visited, then use it as the core of a new section
			// Otherwise, ignore it, since it was already processed
			if (!distances.containsKey(node))
			{
				// Perform a breadth-first search to tag surrounding nodes with distances
				distances.put(node, 0);
				ordering.add(node);
				section.clear();
				
				while (!ordering.isEmpty())
				{
					current = ordering.remove();
					distance = distances.get(current) + 1;
					
					if (distance <= MAX_DISTANCE + 1)
					{
						section.add(current);
						
						// Visit neighboring nodes and assign them distances, if they don't
						// have a distance assigned already
						for (IEdge<PartitionNode, DoorwayData> edge : current.inbound())
						{
							neighbor = edge.head();
							if (!distances.containsKey(neighbor))
							{
								distances.put(neighbor, distance);
								ordering.add(neighbor);
							}
						}
						for (IEdge<PartitionNode, DoorwayData> edge : current.outbound())
						{
							neighbor = edge.tail();
							if (!distances.containsKey(neighbor))
							{
								distances.put(neighbor, distance);
								ordering.add(neighbor);
							}
						}
					}
					else
					{
						removals.add(current);
						break;
					}
				}
				
				// List nodes that have a distance of exactly MAX_DISTANCE + 1
				// Those are precisely the nodes that remain in the queue
				// We can't remove them immediately because that could break
				// the iterator for the graph.
				while (!ordering.isEmpty())
				{
					removals.add(ordering.remove());
				}
				
				// Check if this section contains enough rooms
				if (section.size() >= MIN_SECTION_ROOMS)
				{
					cores.add(node);
				}
				else
				{
					removals.addAll(section);
				}
			}
		}
		
		// Remove all the nodes that were listed for removal
		for (IGraphNode<PartitionNode, DoorwayData> node : removals)
		{
			roomGraph.removeNode(node);
		}
		return cores;
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
