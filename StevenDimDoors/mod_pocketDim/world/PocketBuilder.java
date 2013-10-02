package StevenDimDoors.mod_pocketDim.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.blocks.DimensionalDoor;
import StevenDimDoors.mod_pocketDim.blocks.IDimDoor;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfig;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.util.Pair;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.items.BaseItemDoor;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;

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

	/**
	 * Method that takes an arbitrary link into a dungeon pocket and tries to regenerate it. First uses the origin to find that link, 
	 * then uses that link to find the link that originally created the dungeon. If it cant find any of these, it 
	 * instead makes the link that lead to this point into an exit door style link, sending the player to the overworld. 
	 * @param dimension The dungeon to be regenerated
	 * @param linkIn The link leading somewhere into the dungeon. 
	 * @param properties
	 * @return 
	 */
	public static boolean regenerateDungeonPocket(NewDimData dimension, DimLink linkIn, DDProperties properties)
	{
		if (linkIn == null)
		{
			throw new IllegalArgumentException("link cannot be null.");
		}
		if (properties == null)
		{
			throw new IllegalArgumentException("properties cannot be null.");
		}
		//The link that is at the origin of the dungeon
		DimLink originLink = dimension.getLink(dimension.origin());
		Point4D oldLinkPos = linkIn.source();
		if(originLink==null)
		{
			int orientation = linkIn.orientation();
			originLink=dimension.createLink(oldLinkPos, LinkTypes.SAFE_EXIT, (orientation+2)%4);
			return false;
		}
		//The link that originally created the dungeon on the way in
		DimLink incomingLink = PocketManager.getLink(originLink.destination());
		if(incomingLink==null||incomingLink.linkType()!=LinkTypes.DUNGEON||!(originLink.linkType()==LinkTypes.REVERSE))
		{
			int orientation = linkIn.orientation();
			dimension.deleteLink(originLink);
			dimension.createLink(oldLinkPos, LinkTypes.SAFE_EXIT, (orientation+2)%4);
			return false;
		}
		NewDimData parent = PocketManager.getDimensionData(incomingLink.source().getDimension());
		
		if (!dimension.isDungeon())
		{
			throw new IllegalArgumentException("destination must be dungeon");
		}
		if (dimension.isFilled())
		{
			throw new IllegalArgumentException("destination must be empty");
		}
		if (!dimension.isInitialized())
		{
			throw new IllegalArgumentException("destination must already exist");
		}
		
		try
		{
			//Load a world
			World world = PocketManager.loadDimension(dimension.id());
			
			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a dungeon!");
				return false;
			}
			
			Point3D destination = new Point3D(incomingLink.destination());
			loadAndValidateDungeon(dimension.dungeon(),properties).copyToWorld(world, destination, originLink.orientation(), incomingLink, random);
			dimension.setFilled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		
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
		

		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, true);
			
			//Load a world
			World world = PocketManager.loadDimension(dimension.id());
			
			if (world == null || world.provider == null)
			{
				System.err.println("Could not initialize dimension for a dungeon!");
				return false;
			}
			
			//Choose a dungeon to generate
			Pair<DungeonData, DungeonSchematic> pair = selectDungeon(dimension, random, properties);
			if (pair == null)
			{
				System.err.println("Could not select a dungeon for generation!");
				return false;
			}
			DungeonData dungeon = pair.getFirst();
			DungeonSchematic schematic = pair.getSecond();
			
			//Calculate the destination point
			DungeonPackConfig packConfig = dungeon.dungeonType().Owner != null ? dungeon.dungeonType().Owner.getConfig() : null;
			Point4D source = link.source();
			int orientation = getDoorOrientation(source, properties);
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
			schematic.copyToWorld(world, destination, orientation, link, random);
			
			//Finish up destination initialization
			dimension.initializeDungeon(destination.getX(), destination.getY(), destination.getZ(), orientation, link, dungeon);
			dimension.setFilled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
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

	private static Pair<DungeonData, DungeonSchematic> selectDungeon(NewDimData dimension, Random random, DDProperties properties)
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

	public static boolean generateNewPocket(DimLink link, DDProperties properties, Block door)
	{
		return generateNewPocket(link, DEFAULT_POCKET_SIZE, DEFAULT_POCKET_WALL_THICKNESS, properties, door);
	}
	
	private static int getDoorOrientation(Point4D source, DDProperties properties)
	{
		World world = DimensionManager.getWorld(source.getDimension());
		if (world == null)
		{
			throw new IllegalStateException("The link's source world should be loaded!");
		}

		//Check if the block below that point is actually a door
		int blockID = world.getBlockId(source.getX(), source.getY() - 1, source.getZ());
		if (blockID != properties.DimensionalDoorID && blockID != properties.WarpDoorID &&
			blockID != properties.TransientDoorID &&
			blockID != properties.GoldDimDoorID)
		{
			throw new IllegalStateException("The link's source is not a door block. It should be impossible to traverse a rift without a door!");
		}
		
		//Return the orientation portion of its metadata
		int orientation = world.getBlockMetadata(source.getX(), source.getY() - 1, source.getZ()) & 3;
		return orientation;
	}

	public static boolean generateNewPocket(DimLink link, int size, int wallThickness, DDProperties properties, Block door)
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
		
		try
		{
			//Register a new dimension
			NewDimData parent = PocketManager.getDimensionData(link.source().getDimension());
			NewDimData dimension = PocketManager.registerPocket(parent, false);
			
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
			DimLink reverseLink = dimension.createLink(source.getX(), destinationY, source.getZ(), LinkTypes.REVERSE);
			parent.setDestination(reverseLink, source.getX(), source.getY(), source.getZ());
			
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
		buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2), properties.PermaFabricBlockID, false, 0);

		//Build the (wallThickness - 1) layers of Fabric of Reality
		for (int layer = 1; layer < wallThickness; layer++)
		{
			buildBox(world, center.getX(), center.getY(), center.getZ(), (size / 2) - layer, properties.FabricBlockID,
				layer < (wallThickness - 1) && properties.TNFREAKINGT_Enabled, properties.NonTntWeight);
		}
		
		//Build the door
		int doorOrientation = BlockRotator.transformMetadata(BlockRotator.EAST_DOOR_METADATA, orientation - BlockRotator.EAST_DOOR_METADATA + 2, properties.DimensionalDoorID);
		ItemDimensionalDoor.placeDoorBlock(world, x, y - 1, z, doorOrientation, doorBlock);
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
