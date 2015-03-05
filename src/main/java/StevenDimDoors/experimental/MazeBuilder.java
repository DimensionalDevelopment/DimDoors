package StevenDimDoors.experimental;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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
		SphereDecayOperation decay = new SphereDecayOperation(random, Blocks.air, 0, Blocks.stonebrick, 2);
		
		buildRooms(design.getRoomGraph(), world, offset);
		carveDoorways(design.getRoomGraph(), world, offset, decay, random);
		
		//placeDoors(design, world, offset);
		
		applyRandomDestruction(design, world, offset, decay, random);
	}
	
	private static void applyRandomDestruction(MazeDesign design, World world,
			Point3D offset, SphereDecayOperation decay, Random random)
	{
		//final int DECAY_BOX_SIZE = 8  
	}

	private static void buildRooms(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, Point3D offset)
	{
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			PartitionNode room = node.data();
			buildBox(world, offset, room.minCorner(), room.maxCorner(), Blocks.stonebrick, 0);
		}
	}
	
	private static void carveDoorways(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world,
			Point3D offset, SphereDecayOperation decay, Random random)
	{	
		char axis;
		Point3D lower;
		DoorwayData doorway;
		
		for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes())
		{
			for (IEdge<PartitionNode, DoorwayData> passage : node.outbound())
			{
				doorway = passage.data();
				axis = doorway.axis();
				lower = doorway.minCorner();
				carveDoorway(world, axis, offset.getX() + lower.getX(), offset.getY() + lower.getY(),
						offset.getZ() + lower.getZ(), doorway.width(), doorway.height(), doorway.length(),
						decay, random);
			}
		}
	}
	
	private static void carveDoorway(World world, char axis, int x, int y, int z, int width, int height,
			int length, SphereDecayOperation decay, Random random)
	{
		final int MIN_DOUBLE_DOOR_SPAN = 10;
		
		int gap;
		switch (axis)
		{
			case DoorwayData.X_AXIS:
				if (length >= MIN_DOUBLE_DOOR_SPAN)
				{
					gap = (length - 2) / 3;
					carveDoorAlongX(world, x, y + 1, z + gap);
					carveDoorAlongX(world, x, y + 1, z + length - gap - 1);
				}
				else if (length > 3)
				{
					switch (random.nextInt(3))
					{
						case 0:
							carveDoorAlongX(world, x, y + 1, z + (length - 1) / 2);
							break;
						case 1:
							carveDoorAlongX(world, x, y + 1, z + 2);
							break;
						case 2:
							carveDoorAlongX(world, x, y + 1, z + length - 3);
							break;
					}
				}
				else
				{
					carveDoorAlongX(world, x, y + 1, z + 1);
				}
				break;
			case DoorwayData.Z_AXIS:
				if (width >= MIN_DOUBLE_DOOR_SPAN)
				{
					gap = (width - 2) / 3;
					carveDoorAlongZ(world, x + gap, y + 1, z);
					carveDoorAlongZ(world, x + width - gap - 1, y + 1, z);
				}
				else if (length > 3)
				{
					switch (random.nextInt(3))
					{
						case 0:
							carveDoorAlongZ(world, x + (width - 1) / 2, y + 1, z);
							break;
						case 1:
							carveDoorAlongZ(world, x + 2, y + 1, z);
							break;
						case 2:
							carveDoorAlongZ(world, x + width - 3, y + 1, z);
							break;
					}
				}
				else
				{
					carveDoorAlongZ(world, x + 1, y + 1, z);
				}
				break;
			case DoorwayData.Y_AXIS:
				gap = Math.min(width, length) - 2;
				if (gap > 1)
				{
					if (gap > 6)
					{
						gap = 6;
					}
					decay.apply(world,
							x + random.nextInt(width - gap - 1) + 1, y - 1,
							z + random.nextInt(length - gap - 1) + 1, gap, 4, gap);
				}
				else
				{
					carveHole(world, x + 1, y, z + 1);
				}
				break;
		}
	}
	
	private static void carveDoorAlongX(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, Blocks.air, 0);
		setBlockDirectly(world, x, y + 1, z, Blocks.air, 0);
		setBlockDirectly(world, x + 1, y, z, Blocks.air, 0);
		setBlockDirectly(world, x + 1, y + 1, z, Blocks.air, 0);
	}
	
	private static void carveDoorAlongZ(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, Blocks.air, 0);
		setBlockDirectly(world, x, y + 1, z, Blocks.air, 0);
		setBlockDirectly(world, x, y, z + 1, Blocks.air, 0);
		setBlockDirectly(world, x, y + 1, z + 1, Blocks.air, 0);
	}
	
	private static void carveHole(World world, int x, int y, int z)
	{
		setBlockDirectly(world, x, y, z, Blocks.air, 0);
		setBlockDirectly(world, x, y + 1, z, Blocks.air, 0);
	}

	
	private static void buildBox(World world, Point3D offset, Point3D minCorner, Point3D maxCorner, Block block, int metadata)
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
				setBlockDirectly(world, x, minY, z, block, metadata);
				setBlockDirectly(world, x, maxY, z, block, metadata);
			}
		}
		for (x = minX; x <= maxX; x++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, x, y, minZ, block, metadata);
				setBlockDirectly(world, x, y, maxZ, block, metadata);
			}
		}
		for (z = minZ; z <= maxZ; z++)
		{
			for (y = minY; y <= maxY; y++)
			{
				setBlockDirectly(world, minX, y, z, block, metadata);
				setBlockDirectly(world, maxX, y, z, block, metadata);
			}
		}
	}
	
	private static void setBlockDirectly(World world, int x, int y, int z, Block block, int metadata)
	{
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
		extBlockStorage.func_150818_a(localX, y & 15, localZ, block);
		extBlockStorage.setExtBlockMetadata(localX, y & 15, localZ, metadata);
		chunk.setChunkModified();
	}
}
