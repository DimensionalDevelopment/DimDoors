package StevenDimDoors.mod_pocketDim.world;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.experimental.BoundingBox;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.IDimDoor;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfig;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.util.Pair;
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

	private static boolean buildDungeonPocket(DungeonData dungeon, NewDimData dimension, DimLink link, DungeonSchematic schematic, World world, DDProperties properties)
	{
		//Calculate the destination point
		DungeonPackConfig packConfig = dungeon.dungeonType().Owner != null ? dungeon.dungeonType().Owner.getConfig() : null;
		Point4D source = link.source();
		int orientation = link.orientation();
		Point3D destination;

		if (packConfig != null && packConfig.doDistortDoorCoordinates())
		{
			destination = calculateNoisyDestination(source, dimension, dungeon, orientation);
		}
		else
		{
			destination = new Point3D(source.getX(), source.getY(), source.getZ());
		}

		destination.setY( yCoordHelper.adjustDestinationY(destination.getY(), world.getHeight(), schematic.getEntranceDoorLocation().getY(), schematic.getHeight()) );

		//Generate the dungeon
		schematic.copyToWorld(world, destination, orientation, link, random, properties, false);

		//Finish up destination initialization
		dimension.initializeDungeon(destination.getX(), destination.getY(), destination.getZ(), orientation, link, dungeon);
		dimension.setFilled(true);
		
		return true;    
	}

	public static boolean generateSelectedDungeonPocket(DimLink link, DDProperties properties, DungeonData dungeon)
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
		if (dungeon == null)
		{
			throw new IllegalArgumentException("dungeon cannot be null.");
		}

		// Try to load up the schematic
		DungeonSchematic schematic = null;
		schematic = loadAndValidateDungeon(dungeon, properties);
		if (schematic == null)
		{
			return false;
		}
		
		// Register a new dimension
		NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
		NewDimData dimension = PocketManager.registerPocket(parent, DimensionType.DUNGEON);

		//Load a world
		World world = PocketManager.loadDimension(dimension.id());

		if (world == null || world.provider == null)
		{
			System.err.println("Could not initialize dimension for a dungeon!");
			return false;
		}

		return PocketBuilder.buildDungeonPocket(dungeon, dimension, link, schematic, world, properties);
	}


	public static boolean generateNewDungeonPocket(DimLink link, DDProperties properties)
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

		//Choose a dungeon to generate
		NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
		Pair<DungeonData, DungeonSchematic> pair = selectNextDungeon(parent, random, properties);
		if (pair == null)
		{
			System.err.println("Could not select a dungeon for generation!");
			return false;
		}
		DungeonData dungeon = pair.getFirst();
		DungeonSchematic schematic = pair.getSecond();

		//Register a new dimension
		NewDimData dimension = PocketManager.registerPocket(parent, DimensionType.DUNGEON);

		//Load a world
		World world = PocketManager.loadDimension(dimension.id());

		if (world == null || world.provider == null)
		{
			System.err.println("Could not initialize dimension for a dungeon!");
			return false;
		}
		
		return buildDungeonPocket(dungeon, dimension, link, schematic, world, properties);
	}


	private static Point3D calculateNoisyDestination(Point4D source, NewDimData dimension, DungeonData dungeon, int orientation)
	{
		int depth = NewDimData.calculatePackDepth(dimension.parent(), dungeon);
		int forwardNoise = MathHelper.getRandomIntegerInRange(random, 10 * depth, 130 * depth);
		int sidewaysNoise = MathHelper.getRandomIntegerInRange(random, -10 * depth, 10 * depth);

		//Rotate the link destination noise to point in the same direction as the door exit
		//and add it to the door's location. Use EAST as the reference orientation since linkDestination
		//is constructed as if pointing East.
		Point3D linkDestination = new Point3D(forwardNoise, 0, sidewaysNoise);
		Point3D sourcePoint = new Point3D(source.getX(), source.getY(), source.getZ());
		Point3D zeroPoint = new Point3D(0, 0, 0);
		BlockRotator.transformPoint(linkDestination, zeroPoint, orientation - BlockRotator.EAST_DOOR_METADATA, sourcePoint);
		return linkDestination;
	}

	private static Pair<DungeonData, DungeonSchematic> selectNextDungeon(NewDimData parent, Random random, DDProperties properties)
	{
		DungeonData dungeon = null;
		DungeonSchematic schematic = null;

		dungeon = DungeonHelper.instance().selectNextDungeon(parent, random);

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
		return new Pair<DungeonData, DungeonSchematic>(dungeon, schematic);
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

	public static boolean generateNewPocket(DimLink link, DDProperties properties, Block door, DimensionType type)
	{
		return generateNewPocket(link, DEFAULT_POCKET_SIZE, DEFAULT_POCKET_WALL_THICKNESS, properties, door, type);
	}

	private static int getDoorOrientation(Point4D source, DDProperties properties)
	{
		World world = DimensionManager.getWorld(source.getDimension());
		if (world == null)
		{
			throw new IllegalStateException("The link's source world should be loaded!");
		}

		//Check if the block below that point is actually a door
		Block block = world.getBlock(source.getX(), source.getY() - 1, source.getZ());
		if (block==null || !(block instanceof IDimDoor))
		{
			throw new IllegalStateException("The link's source is not a door block. It should be impossible to traverse a rift without a door!");
		}

		//Return the orientation portion of its metadata
		int orientation = world.getBlockMetadata(source.getX(), source.getY() - 1, source.getZ()) & 3;
		return orientation;
	}
	
	public static void validatePocketSetup(DimLink link, int size, int wallThickness, DDProperties properties, Block door)
	{
		if (link == null)
		{
			throw new IllegalArgumentException();
		}
		if (properties == null)
		{
			throw new IllegalArgumentException("properties cannot be null.");
		}
		if (link.linkType() != LinkType.PERSONAL && link.hasDestination())
		{
			throw new IllegalArgumentException("link cannot have a destination assigned already.");
		}

		if(door==null)
		{
			throw new IllegalArgumentException("Must have a doorItem to gen one!!");

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
	}
	
	/**I know this is almost a copy of generateNewPocket, but we might want to change other things.
	 * 
	 * @param link
	 * @param properties
	 * @param player
	 * @param door
	 * @return
	 */
	public static boolean generateNewPersonalPocket(DimLink link, DDProperties properties,EntityPlayer player, Block door)
	{
		//incase a chicken walks in or something
		if(!(player instanceof EntityPlayer))
		{
			return false;
		}
		int wallThickness = DEFAULT_POCKET_WALL_THICKNESS;
		int size = DEFAULT_POCKET_SIZE;
		
		validatePocketSetup(link, size, wallThickness, properties, door);
		
		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, DimensionType.PERSONAL, player.getGameProfile().getId().toString());


			//Load a world
			World world = PocketManager.loadDimension(dimension.id());

			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a pocket!");
				return false;
			}

			//Calculate the destination point
			Point4D source = link.source();
			int destinationY = yCoordHelper.adjustDestinationY(link.source().getY(), world.getHeight(), wallThickness + 1, size);
			int orientation = getDoorOrientation(source, properties);

			//Place a link leading back out of the pocket
			DimLink reverseLink = dimension.createLink(source.getX(), destinationY, source.getZ(), LinkType.REVERSE,(link.orientation()+2)%4);
			parent.setLinkDestination(reverseLink, source.getX(), source.getY(), source.getZ());

			//Build the actual pocket area
			buildPocket(world, source.getX(), destinationY, source.getZ(), orientation, size, wallThickness, properties, door);

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
	
	public static boolean generateNewPocket(DimLink link, int size, int wallThickness, DDProperties properties, Block door, DimensionType type)
	{
		validatePocketSetup(link, size, wallThickness, properties, door);
		
		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, type);


			//Load a world
			World world = PocketManager.loadDimension(dimension.id());

			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a pocket!");
				return false;
			}

			//Calculate the destination point
			Point4D source = link.source();
			int destinationY = yCoordHelper.adjustDestinationY(source.getY(), world.getHeight(), wallThickness + 1, size);
			int orientation = getDoorOrientation(source, properties);

			//Place a link leading back out of the pocket

			DimLink reverseLink = dimension.createLink(source.getX(), destinationY, source.getZ(), LinkType.REVERSE,(link.orientation()+2)%4);
			parent.setLinkDestination(reverseLink, source.getX(), source.getY(), source.getZ());

			//Build the actual pocket area
			buildPocket(world, source.getX(), destinationY, source.getZ(), orientation, size, wallThickness, properties, door);

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

	private static void buildPocket(World world, int x, int y, int z, int orientation, int size, int wallThickness, DDProperties properties, Block doorBlock)
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
		if (!(doorBlock instanceof IDimDoor))
		{
			throw new IllegalArgumentException("Door must implement IDimDoor");
		}


		Point3D center = new Point3D(x - wallThickness + 1 + (size / 2), y - wallThickness - 1 + (size / 2), z);
		Point3D door = new Point3D(x, y, z);
		BlockRotator.transformPoint(center, door, orientation - BlockRotator.EAST_DOOR_METADATA, door);

		//Build the outer layer of Eternal Fabric
		buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2), mod_pocketDim.blockDimWallPerm, 0, false, 0);

		//check if we are building a personal pocket
		int metadata = 0;
		if(world.provider instanceof PersonalPocketProvider)
		{
			metadata = 2;
		}
		
		//Build the (wallThickness - 1) layers of Fabric of Reality
		for (int layer = 1; layer < wallThickness; layer++)
		{
			buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2) - layer, mod_pocketDim.blockDimWall, metadata,
					layer < (wallThickness - 1) && properties.TNFREAKINGT_Enabled, properties.NonTntWeight);
		}
		
		//MazeBuilder.generate(world, x, y, z, random);

		//Build the door
		int doorOrientation = BlockRotator.transformMetadata(BlockRotator.EAST_DOOR_METADATA, orientation - BlockRotator.EAST_DOOR_METADATA + 2, doorBlock);
		ItemDimensionalDoor.placeDoorBlock(world, x, y - 1, z, doorOrientation, doorBlock);

	}

	private static void buildBox(World world, int centerX, int centerY, int centerZ, int radius, Block block, int metadata, boolean placeTnt, int nonTntWeight)
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
				setBlockDirectlySpecial(world, x, startY, z, block, metadata, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, x, endY, z, block, metadata, placeTnt, nonTntWeight);
			}

			for (y = startY; y <= endY; y++)
			{
				setBlockDirectlySpecial(world, x, y, startZ, block, metadata, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, x, y, endZ, block, metadata, placeTnt, nonTntWeight);
			}
		}

		for (y = startY; y <= endY; y++)
		{
			for (z = startZ; z <= endZ; z++)
			{
				setBlockDirectlySpecial(world, startX, y, z, block, metadata, placeTnt, nonTntWeight);
				setBlockDirectlySpecial(world, endX, y, z, block, metadata, placeTnt, nonTntWeight);
			}
		}
	}

	private static void setBlockDirectlySpecial(World world, int x, int y, int z, Block block, int metadata, boolean placeTnt, int nonTntWeight)
	{
		if (placeTnt && random.nextInt(nonTntWeight + 1) == 0)
		{
			setBlockDirectly(world, x, y, z, Blocks.tnt, 1);
		}
		else
		{
			setBlockDirectly(world, x, y, z, block, metadata);
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

	public static BoundingBox calculateDefaultBounds(NewDimData pocket)
	{
		// Calculate the XZ bounds of this pocket assuming that it has the default size
		// The Y bounds will be set to encompass the height of a chunk.
		
		int minX = 0;
		int minZ = 0;
		Point4D origin = pocket.origin();
		int orientation = pocket.orientation();
		if (orientation < 0 || orientation > 3)
		{
			throw new IllegalArgumentException("pocket has an invalid orientation value.");
		}
		switch (orientation)
		{
		case 0:
			minX = origin.getX() - DEFAULT_POCKET_WALL_THICKNESS + 1;
			minZ = origin.getZ() - DEFAULT_POCKET_SIZE / 2;
			break;
		case 1:
			minX = origin.getX() - DEFAULT_POCKET_SIZE / 2;
			minZ = origin.getZ() - DEFAULT_POCKET_WALL_THICKNESS + 1;
			break;
		case 2:
			minX = origin.getX() + DEFAULT_POCKET_WALL_THICKNESS - DEFAULT_POCKET_SIZE;
			minZ = origin.getZ() - DEFAULT_POCKET_SIZE / 2;
			break;
		case 3:
			minX = origin.getX() - DEFAULT_POCKET_SIZE / 2;
			minZ = origin.getZ() + DEFAULT_POCKET_WALL_THICKNESS - DEFAULT_POCKET_SIZE;
			break;
		}
		return new BoundingBox(minX, 0, minZ, DEFAULT_POCKET_SIZE, 255, DEFAULT_POCKET_SIZE);
	}
}
