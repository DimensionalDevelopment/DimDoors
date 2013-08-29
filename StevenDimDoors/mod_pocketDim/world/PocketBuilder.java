package StevenDimDoors.mod_pocketDim.world;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfig;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class PocketBuilder
{
	public static final int MIN_POCKET_SIZE = 5;
	public static final int MAX_POCKET_SIZE = 51;
	public static final int DEFAULT_POCKET_SIZE = 39;
	
	public static final int MIN_POCKET_WALL_THICKNESS = 1;
	public static final int MAX_POCKET_WALL_THICKNESS = 10;
	public static final int DEFAULT_POCKET_WALL_THICKNESS = 5;
	
	private static final Random random = new Random();

	private PocketBuilder() { }

	public static boolean initializeDestination(IDimLink link, DDProperties properties)
	{
		if (link.hasDestination())
		{
			return true;
		}

		//Check the destination type and respond accordingly
		switch (link.linkType())
		{
			case IDimLink.TYPE_DUNGEON:
				return generateNewDungeonPocket(link, properties);
			case IDimLink.TYPE_POCKET:
				return generateNewPocket(link, properties);
			default:
				throw new IllegalArgumentException("link has an unrecognized link type.");
		}
	}

	public static boolean generateNewDungeonPocket(IDimLink link, DDProperties properties)
	{
		if (link == null)
		{
			throw new IllegalArgumentException("link cannot be null.");
		}
		if (properties == null)
		{
			throw new IllegalArgumentException("properties cannot be null.");
		}
		if (link.hasDestination())
		{
			throw new IllegalArgumentException("link cannot have a destination assigned already.");
		}

		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, false);
			
			//Load a world
			World world = DimensionManager.getWorld(dimension.id());
			
			if (world == null)
			{
				DimensionManager.initDimension(dimension.id());
				world = DimensionManager.getWorld(dimension.id());
			}
			if (world != null && world.provider == null)
			{
				DimensionManager.initDimension(dimension.id());
			}
			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a dungeon!");
				return false;
			}
			
			/* This code is currently wrong. It's missing the following things:
			 * 1. Calculate the destination point for real. That includes adding door noise if needed.
			 * 2. Receive the DungeonData from selectDungeon()
			 * 3. The function signature for DungeonSchematic.copyToWorld() has to be rewritten.
			 */
			
			//Choose a dungeon to generate
			DungeonSchematic schematic = selectDungeon(dimension, random, properties);
			
			if (schematic == null)
			{
				System.err.println("Could not select a dungeon for generation!");
				return false;
			}
			
			//Calculate the destination point
			Point4D source = link.source();
			int destinationY = yCoordHelper.adjustDestinationY(destination, world.getHeight(), schematic.getEntranceDoorLocation().getY(), schematic.getHeight());
			int orientation = getDestinationOrientation(source);
			destination.setY(destinationY);
			
			//Generate the dungeon
			DungeonPackConfig packConfig = dungeon.dungeonType().Owner != null ? dungeon.dungeonType().Owner.getConfig() : null;

			schematic.copyToWorld(world, link, packConfig.doDistortDoorCoordinates());
			
			//Finish up destination initialization
			dimension.initializePocket(destination.getX(), destination.getY(), destination.getZ(), orientation, link);
			dimension.setFilled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private static DungeonSchematic selectDungeon(NewDimData dimension, Random random, DDProperties properties)
	{
		//We assume the dimension doesn't have a dungeon assigned
		if (dimension.dungeon() != null)
		{
			throw new IllegalArgumentException("dimension cannot have a dungeon assigned already.");
		}
		
		DungeonData dungeon = null;
		DungeonSchematic schematic = null;

		dungeon = DungeonHelper.instance().selectDungeon(dimension, random);
		
		if (dungeon != null)
		{
			schematic = loadAndValidateDungeon(dungeon, properties);
		}
		else
		{
			System.err.println("Could not select a dungeon at all!");
		}
		
		if (schematic == null)
		{
			//TODO: In the future, remove this dungeon from the generation lists altogether.
			//That will have to wait until our code is updated to support that more easily.

			try
			{
				System.err.println("Loading the default error dungeon instead...");
				dungeon = DungeonHelper.instance().getDefaultErrorDungeon();
				schematic = loadAndValidateDungeon(dungeon, properties);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return schematic;
	}
	
	private static DungeonSchematic loadAndValidateDungeon(DungeonData dungeon, DDProperties properties)
	{
		try
		{
			DungeonSchematic schematic = dungeon.loadSchematic();
			
			//Validate the dungeon's dimensions
			if (hasValidDimensions(schematic))
			{
				schematic.applyImportFilters(properties);

				//Check that the dungeon has an entrance or we'll have a crash
				if (schematic.getEntranceDoorLocation() == null)
				{
					System.err.println("The following schematic file does not have an entrance: " + dungeon.schematicPath());
					return null;
				}
			}
			else
			{
				System.err.println("The following schematic file has dimensions that exceed the maximum permitted dimensions for dungeons: " + dungeon.schematicPath());
				return null;
			}
			return schematic;
		}
		catch (Exception e)
		{
			System.err.println("An error occurred while loading the following schematic: " + dungeon.schematicPath());
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	private static boolean hasValidDimensions(DungeonSchematic schematic)
	{
		return (schematic.getWidth() <= DungeonHelper.MAX_DUNGEON_WIDTH &&
			schematic.getHeight() <= DungeonHelper.MAX_DUNGEON_HEIGHT &&
			schematic.getLength() <= DungeonHelper.MAX_DUNGEON_LENGTH);
	}

	public static boolean generateNewPocket(IDimLink link, DDProperties properties)
	{
		return generateNewPocket(link, DEFAULT_POCKET_SIZE, DEFAULT_POCKET_WALL_THICKNESS, properties);
	}
	
	private static int getDestinationOrientation(Point4D source)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public static boolean generateNewPocket(IDimLink link, int size, int wallThickness, DDProperties properties)
	{
		if (link == null)
		{
			throw new IllegalArgumentException();
		}
		if (properties == null)
		{
			throw new IllegalArgumentException("properties cannot be null.");
		}
		if (link.hasDestination())
		{
			throw new IllegalArgumentException("link cannot have a destination assigned already.");
		}
		
		if (size < MIN_POCKET_SIZE || size > MAX_POCKET_SIZE)
		{
			throw new IllegalArgumentException("size must be between " + MIN_POCKET_SIZE + " and " + MAX_POCKET_SIZE + ", inclusive.");
		}
		if (wallThickness < MIN_POCKET_WALL_THICKNESS || wallThickness > MAX_POCKET_WALL_THICKNESS)
		{
			throw new IllegalArgumentException("wallThickness must be between " + MIN_POCKET_WALL_THICKNESS + " and " + MAX_POCKET_WALL_THICKNESS + ", inclusive.");
		}
		if (size % 2 == 0)
		{
			throw new IllegalArgumentException("size must be an odd number.");
		}
		if (size < 2 * wallThickness + 3)
		{
			throw new IllegalArgumentException("size must be large enough to fit the specified wall thickness and some air space.");
		}
		
		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, false);
			
			//Load a world
			World world = DimensionManager.getWorld(dimension.id());
			
			if (world == null)
			{
				DimensionManager.initDimension(dimension.id());
				world = DimensionManager.getWorld(dimension.id());
			}
			if (world != null && world.provider == null)
			{
				DimensionManager.initDimension(dimension.id());
			}
			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a pocket!");
				return false;
			}
			
			//Calculate the destination point
			Point4D source = link.source();
			int destinationY = yCoordHelper.adjustDestinationY(source.getY(), world.getHeight(), wallThickness + 1, size);
			int orientation = getDestinationOrientation(source);
			
			//Build the actual pocket area
			buildPocket(world, source.getX(), destinationY, source.getZ(), orientation, size, wallThickness, properties);
			
			//Finish up destination initialization
			dimension.initializePocket(source.getX(), destinationY, source.getZ(), orientation, link);
			dimension.setFilled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private static void buildPocket(World world, int x, int y, int z, int orientation, int size, int wallThickness, DDProperties properties)
	{
		if (properties == null)
		{
			throw new IllegalArgumentException("properties cannot be null.");
		}
		if (size < MIN_POCKET_SIZE || size > MAX_POCKET_SIZE)
		{
			throw new IllegalArgumentException("size must be between " + MIN_POCKET_SIZE + " and " + MAX_POCKET_SIZE + ", inclusive.");
		}
		if (wallThickness < MIN_POCKET_WALL_THICKNESS || wallThickness > MAX_POCKET_WALL_THICKNESS)
		{
			throw new IllegalArgumentException("wallThickness must be between " + MIN_POCKET_WALL_THICKNESS + " and " + MAX_POCKET_WALL_THICKNESS + ", inclusive.");
		}
		if (size % 2 == 0)
		{
			throw new IllegalArgumentException("size must be an odd number.");
		}
		if (size < 2 * wallThickness + 3)
		{
			throw new IllegalArgumentException("size must be large enough to fit the specified wall thickness and some air space.");
		}
		
		Point3D center = new Point3D(x - wallThickness + 1 + (size / 2), y - wallThickness - 1 + (size / 2), z);
		Point3D door = new Point3D(x, y, z);
		BlockRotator.transformPoint(center, door, orientation - BlockRotator.EAST_DOOR_METADATA, door);
		
		//Build the outer layer of Eternal Fabric
		buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2), properties.PermaFabricBlockID, false, 0);

		//Build the (wallThickness - 1) layers of Fabric of Reality
		for (int layer = 1; layer < wallThickness; layer++)
		{
			buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2) - layer, properties.FabricBlockID,
				layer < (wallThickness - 1) && properties.TNFREAKINGT_Enabled, properties.NonTntWeight);
		}
				
		//Build the door
		int metadata = BlockRotator.transformMetadata(BlockRotator.EAST_DOOR_METADATA, orientation - BlockRotator.EAST_DOOR_METADATA, properties.DimensionalDoorID);
		setBlockDirectly(world, x, y, z, properties.DimensionalDoorID, metadata);
		setBlockDirectly(world, x, y - 1, z, properties.DimensionalDoorID, metadata);
	}

	private static void buildBox(World world, int centerX, int centerY, int centerZ, int radius, int blockID, boolean placeTnt, int nonTntWeight)
	{
		int x, y, z;
		
		final int startX = centerX - radius;
		final int startY = centerY - radius;
		final int startZ = centerZ - radius;
		
		final int endX = centerX + radius;
		final int endY = centerY + radius;
		final int endZ = centerZ + radius;
		
		//Build faces of the box
		for (x = startX; x <= endX; x++)
		{
			for (z = startZ; z <= endZ; z++)
			{
				setBlockDirectlySpecial(world, x, startY, z, blockID, 0, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, x, endY, z, blockID, 0, placeTnt, nonTntWeight);
			}
			
			for (y = startY; y <= endY; y++)
			{
				setBlockDirectlySpecial(world, x, y, startZ, blockID, 0, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, x, y, endZ, blockID, 0, placeTnt, nonTntWeight);
			}
		}
		
		for (y = startY; y <= endY; y++)
		{
			for (z = startZ; z <= endZ; z++)
			{
				setBlockDirectlySpecial(world, startX, y, z, blockID, 0, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, endX, y, z, blockID, 0, placeTnt, nonTntWeight);
			}
		}
	}

	private static void setBlockDirectlySpecial(World world, int x, int y, int z, int blockID, int metadata, boolean placeTnt, int nonTntWeight)
	{
		if (placeTnt && random.nextInt(nonTntWeight + 1) == 0)
		{
			setBlockDirectly(world, x, y, z, Block.tnt.blockID, 1);
		}
		else
		{
			setBlockDirectly(world, x, y, z, blockID, metadata);
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
