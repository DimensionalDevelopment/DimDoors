package StevenDimDoors.mod_pocketDim.dungeon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.schematic.CompoundFilter;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;
import StevenDimDoors.mod_pocketDim.schematic.ReplacementFilter;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;

public class DungeonSchematic extends Schematic {

	private static final short MAX_VANILLA_BLOCK_ID = 158;
	private static final short STANDARD_FABRIC_OF_REALITY_ID = 1973;
	private static final short STANDARD_ETERNAL_FABRIC_ID = 220;
	private static final short STANDARD_WARP_DOOR_ID = 1975;
	private static final short STANDARD_DIMENSIONAL_DOOR_ID = 1970;
	private static final short MONOLITH_SPAWN_MARKER_ID = (short) Block.endPortalFrame.blockID;
	private static final short EXIT_DOOR_MARKER_ID = (short) Block.sandStone.blockID;
	
	private int orientation;
	private Point3D entranceDoorLocation;
	private ArrayList<Point3D> exitDoorLocations;
	private ArrayList<Point3D> dimensionalDoorLocations;
	private ArrayList<Point3D> monolithSpawnLocations;
	
	private static final short[] MOD_BLOCK_FILTER_EXCEPTIONS = new short[] {
		STANDARD_FABRIC_OF_REALITY_ID,
		STANDARD_ETERNAL_FABRIC_ID,
		STANDARD_WARP_DOOR_ID,
		STANDARD_DIMENSIONAL_DOOR_ID
	};
	
	private DungeonSchematic(Schematic source)
	{
		super(source);
	}
	
	public int getOrientation()
	{
		return orientation;
	}
	
	public Point3D getEntranceDoorLocation()
	{
		return entranceDoorLocation;
	}
	
	private DungeonSchematic()
	{
		//Used to create a dummy instance for readFromResource()
		super((short) 0, (short) 0, (short) 0, null, null, null);
	}

	public static DungeonSchematic readFromFile(String schematicPath) throws FileNotFoundException, InvalidSchematicException
	{
		return readFromFile(new File(schematicPath));
	}

	public static DungeonSchematic readFromFile(File schematicFile) throws FileNotFoundException, InvalidSchematicException
	{
		return readFromStream(new FileInputStream(schematicFile));
	}

	public static DungeonSchematic readFromResource(String resourcePath) throws InvalidSchematicException
	{
		//We need an instance of a class in the mod to retrieve a resource
		DungeonSchematic empty = new DungeonSchematic();
		InputStream schematicStream = empty.getClass().getResourceAsStream(resourcePath);
		return readFromStream(schematicStream);
	}

	public static DungeonSchematic readFromStream(InputStream schematicStream) throws InvalidSchematicException
	{
		return new DungeonSchematic(Schematic.readFromStream(schematicStream));
	}
	
	public void applyImportFilters(DDProperties properties)
	{
		//Search for special blocks (warp doors, dim doors, and end portal frames that mark Monolith spawn points)
		SpecialBlockFinder finder = new SpecialBlockFinder(STANDARD_WARP_DOOR_ID, STANDARD_DIMENSIONAL_DOOR_ID,
				MONOLITH_SPAWN_MARKER_ID, EXIT_DOOR_MARKER_ID);
		applyFilter(finder);
		
		orientation = (finder.getEntranceOrientation() + 2) & 3; //Flip the entrance's orientation to get the dungeon's orientation
		entranceDoorLocation = finder.getEntranceDoorLocation();
		exitDoorLocations = finder.getExitDoorLocations();
		dimensionalDoorLocations = finder.getDimensionalDoorLocations();
		monolithSpawnLocations = finder.getMonolithSpawnLocations();
		
		//Filter out mod blocks except some of our own
		CompoundFilter standardizer = new CompoundFilter();
		standardizer.addFilter(new ModBlockFilter(MAX_VANILLA_BLOCK_ID, MOD_BLOCK_FILTER_EXCEPTIONS,
				(short) properties.FabricBlockID, (byte) 0));
		
		//Also convert standard DD block IDs to local versions
		Map<Short, Short> mapping = getAssignedToStandardIDMapping(properties);
		
		for (Entry<Short, Short> entry : mapping.entrySet())
		{
			if (entry.getKey() != entry.getValue())
			{
				standardizer.addFilter(new ReplacementFilter(entry.getValue(), entry.getKey()));
			}
		}
		applyFilter(standardizer);
	}
	
	public void applyExportFilters(DDProperties properties)
	{		
		//Check if some block IDs assigned by Forge differ from our standard IDs
		//If so, change the IDs to standard values
		CompoundFilter standardizer = new CompoundFilter();
		Map<Short, Short> mapping = getAssignedToStandardIDMapping(properties);
		
		for (Entry<Short, Short> entry : mapping.entrySet())
		{
			if (entry.getKey() != entry.getValue())
			{
				standardizer.addFilter(new ReplacementFilter(entry.getKey(), entry.getValue()));
			}
		}
		
		//Filter out mod blocks except some of our own
		//This comes after ID standardization because the mod block filter relies on standardized IDs
		standardizer.addFilter(new ModBlockFilter(MAX_VANILLA_BLOCK_ID, MOD_BLOCK_FILTER_EXCEPTIONS,
				STANDARD_FABRIC_OF_REALITY_ID, (byte) 0));
		
		applyFilter(standardizer);
	}
	
	private Map<Short, Short> getAssignedToStandardIDMapping(DDProperties properties)
	{
		//If we ever need this broadly or support other mods, this should be moved to a separate class
		TreeMap<Short, Short> mapping = new TreeMap<Short, Short>();
		mapping.put((short) properties.FabricBlockID, STANDARD_FABRIC_OF_REALITY_ID);
		mapping.put((short) properties.PermaFabricBlockID, STANDARD_ETERNAL_FABRIC_ID);
		mapping.put((short) properties.WarpDoorID, STANDARD_WARP_DOOR_ID);
		mapping.put((short) properties.DimensionalDoorID, STANDARD_DIMENSIONAL_DOOR_ID);
		return mapping;
	}
	
	public static DungeonSchematic copyFromWorld(World world, int x, int y, int z, short width, short height, short length, boolean doCompactBounds)
	{
		return new DungeonSchematic(Schematic.copyFromWorld(world, x, y, z, width, height, length, doCompactBounds));
	}

	public void copyToWorld(World world, Point3D pocketCenter, int dungeonOrientation, int originDimID, int destDimID)
	{
		//TODO: This function is an improvised solution so we can get the release moving. In the future,
		//we should generalize block tranformations and implement support for them at the level of Schematic,
		//then just use that support from DungeonSchematic instead of making this local fix.
		//It might be easiest to support transformations using a WorldOperation
		
		final int turnAngle = dungeonOrientation - orientation;
		
		
		int index;
		int count;
		int blockID;
		int blockMeta;
		int dx, dy, dz;
		Point3D pocketPoint = new Point3D(0, 0, 0);
		
		//Copy blocks and metadata into the world
		index = 0;
		for (dy = 0; dy < height; dy++)
		{
			for (dz = 0; dz < length; dz++)
			{
				for (dx = 0; dx < width; dx++)
				{
					pocketPoint.setX(dx);
					pocketPoint.setY(dy);
					pocketPoint.setZ(dz);
					blockID = blocks[index];
					BlockRotator.transformPoint(pocketPoint, entranceDoorLocation, turnAngle, pocketCenter);
					blockMeta = BlockRotator.transformMetadata(metadata[index], turnAngle + BlockRotator.NORTH_DOOR_METADATA, blockID);

					//In the future, we might want to make this more efficient by building whole chunks at a time
					setBlockDirectly(world, pocketPoint.getX(), pocketPoint.getY(), pocketPoint.getZ(), blockID, blockMeta);
					index++;
				}
			}
		}
		//Copy tile entities into the world
		count = tileEntities.tagCount();
		for (index = 0; index < count; index++)
		{
			NBTTagCompound tileTag = (NBTTagCompound) tileEntities.tagAt(index);
			//Rewrite its location to be in world coordinates
			pocketPoint.setX(tileTag.getInteger("x"));
			pocketPoint.setY(tileTag.getInteger("y"));
			pocketPoint.setZ(tileTag.getInteger("z"));
			BlockRotator.transformPoint(pocketPoint, entranceDoorLocation, turnAngle, pocketCenter);
			tileTag.setInteger("x", pocketPoint.getX());
			tileTag.setInteger("y", pocketPoint.getY());
			tileTag.setInteger("z", pocketPoint.getZ());
			//Load the tile entity and put it in the world
			world.setBlockTileEntity(pocketPoint.getX(), pocketPoint.getY(), pocketPoint.getZ(), TileEntity.createAndLoadEntity(tileTag));
		}
		
		setUpDungeon(world, pocketCenter, turnAngle, originDimID, destDimID);
	}
	
	private void setUpDungeon(World world, Point3D pocketCenter, int turnAngle, int originDimID, int destDimID)
	{
		//The following Random initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(world.getSeed());
        long factorA = random.nextLong() / 2L * 2L + 1L;
        long factorB = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((pocketCenter.getX() >> 4) * factorA + (pocketCenter.getZ() >> 4) * factorB ^ world.getSeed());
		
        //Transform dungeon corners
        Point3D minCorner = new Point3D(0, 0, 0);
        Point3D maxCorner = new Point3D(width - 1, height - 1, length - 1);
        transformCorners(entranceDoorLocation, pocketCenter, turnAngle, minCorner, maxCorner);
        
		//Fill empty chests and dispensers
		FillContainersOperation filler = new FillContainersOperation(random);
		filler.apply(world, minCorner, maxCorner);
		
		//Set up entrance door rift
		setUpEntranceDoorLink(world, entranceDoorLocation, turnAngle, pocketCenter);
		
		//Set up link data for dimensional doors
		for (Point3D location : dimensionalDoorLocations)
		{
			setUpDimensionalDoorLink(world, location, entranceDoorLocation, turnAngle, pocketCenter, originDimID, destDimID, random);
		}
		
		//Set up link data for exit door
		for (Point3D location : exitDoorLocations)
		{
			setUpExitDoorLink(world, location, entranceDoorLocation, turnAngle, pocketCenter, originDimID, destDimID, random);
		}
		
		//Remove end portal frames and spawn Monoliths
		for (Point3D location : monolithSpawnLocations)
		{
			spawnMonolith(world, location, entranceDoorLocation, turnAngle, pocketCenter);
		}
	}
	
	private static void transformCorners(Point3D schematicEntrance, Point3D pocketCenter, int turnAngle, Point3D minCorner, Point3D maxCorner)
	{
		int temp;
		BlockRotator.transformPoint(minCorner, schematicEntrance, turnAngle, pocketCenter);
		BlockRotator.transformPoint(maxCorner, schematicEntrance, turnAngle, pocketCenter);
		if (minCorner.getX() > maxCorner.getX())
		{
			temp = minCorner.getX();
			minCorner.setX(maxCorner.getX());
			maxCorner.setX(temp);
		}
		if (minCorner.getY() > maxCorner.getY())
		{
			temp = minCorner.getY();
			minCorner.setY(maxCorner.getY());
			maxCorner.setY(temp);
		}
		if (minCorner.getZ() > maxCorner.getZ())
		{
			temp = minCorner.getZ();
			minCorner.setZ(maxCorner.getZ());
			maxCorner.setZ(temp);
		}
	}
	
	private static void setUpEntranceDoorLink(World world, Point3D entrance, int rotation, Point3D pocketCenter)
	{
		//Set the orientation of the rift exit
		Point3D entranceRiftLocation = entrance.clone();
		BlockRotator.transformPoint(entranceRiftLocation, entrance, rotation, pocketCenter);
		LinkData sideLink = dimHelper.instance.getLinkDataFromCoords(
				entranceRiftLocation.getX(),
				entranceRiftLocation.getY(),
				entranceRiftLocation.getZ(),
				world);
		sideLink.linkOrientation = world.getBlockMetadata(
				entranceRiftLocation.getX(),
				entranceRiftLocation.getY() - 1,
				entranceRiftLocation.getZ());
	}
	
	private static void setUpExitDoorLink(World world, Point3D point, Point3D entrance, int rotation, Point3D pocketCenter, int originDimID, int destDimID, Random random)
	{
		try
		{
			//TODO: Hax, remove this later
			DDProperties properties = DDProperties.instance();
			
			//Transform doorLocation to the pocket coordinate system.
			Point3D location = point.clone();
			BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
			int blockDirection = world.getBlockMetadata(location.getX(), location.getY() - 1, location.getZ());
			Point3D linkDestination = location.clone();
			
			LinkData randomLink = dimHelper.instance.getRandomLinkData(false);
			LinkData sideLink = new LinkData(destDimID,
					dimHelper.instance.getDimData(originDimID).exitDimLink.destDimID,
					location.getX(),
					location.getY(),
					location.getZ(),
					linkDestination.getX(),
					linkDestination.getY() + 1,
					linkDestination.getZ(),
					true, blockDirection);

			if (sideLink.destDimID == properties.LimboDimensionID)
			{
				sideLink.destDimID = 0;
			}
			else if ((random.nextBoolean() && randomLink != null))
			{
				sideLink.destDimID = randomLink.locDimID;
			}
			sideLink.destYCoord = yCoordHelper.getFirstUncovered(sideLink.destDimID, linkDestination.getX(), 10, linkDestination.getZ());

			if (sideLink.destYCoord < 5)
			{
				sideLink.destYCoord = 70;
			}
			sideLink.linkOrientation = world.getBlockMetadata(linkDestination.getX(), linkDestination.getY() - 1, linkDestination.getZ());

			dimHelper.instance.createLink(sideLink);
			dimHelper.instance.createLink(sideLink.destDimID , 
					sideLink.locDimID, 
					sideLink.destXCoord, 
					sideLink.destYCoord, 
					sideLink.destZCoord, 
					sideLink.locXCoord, 
					sideLink.locYCoord, 
					sideLink.locZCoord, 
					dimHelper.instance.flipDoorMetadata(sideLink.linkOrientation));

			if (world.getBlockId(linkDestination.getX(), linkDestination.getY() - 3, linkDestination.getZ()) == properties.FabricBlockID)
			{
				setBlockDirectly(world, linkDestination.getX(), linkDestination.getY() - 2, linkDestination.getZ(), Block.stoneBrick.blockID, 0);
			}
			else
			{
				setBlockDirectly(world,linkDestination.getX(), linkDestination.getY() - 2, linkDestination.getZ(),
						world.getBlockId(linkDestination.getX(), linkDestination.getY() - 3, linkDestination.getZ()),
						world.getBlockMetadata(linkDestination.getX(), linkDestination.getY() - 3, linkDestination.getZ()));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void setUpDimensionalDoorLink(World world, Point3D point, Point3D entrance, int rotation, Point3D pocketCenter, int originDimID, int destDimID, Random random)
	{
		int depth = dimHelper.instance.getDimDepth(originDimID) + 1;
		int forwardNoise = MathHelper.getRandomIntegerInRange(random, -50 * depth, 150 * depth);
		int sidewaysNoise = MathHelper.getRandomIntegerInRange(random, -10 * depth, 10 * depth);
		
		//Transform doorLocation to the pocket coordinate system
		Point3D location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		int blockDirection = world.getBlockMetadata(location.getX(), location.getY() - 1, location.getZ());
		
		//Rotate the link destination noise to point in the same direction as the door exit
		//and add it to the door's location. Use EAST as the reference orientation since linkDestination
		//is constructed as if pointing East.
		Point3D linkDestination = new Point3D(forwardNoise, 0, sidewaysNoise);
		Point3D zeroPoint = new Point3D(0, 0, 0);
		BlockRotator.transformPoint(linkDestination, zeroPoint, blockDirection - BlockRotator.EAST_DOOR_METADATA, location);
		
		//Create the link between our current door and its intended exit in destination pocket
		LinkData sideLink = new LinkData(destDimID, 0,
				location.getX(),
				location.getY(),
				location.getZ(),
				linkDestination.getX(),
				linkDestination.getY() + 1,
				linkDestination.getZ(),
				true, blockDirection);
		dimHelper.instance.createPocket(sideLink, true, true);
	}
	
	private static void spawnMonolith(World world, Point3D point, Point3D entrance, int rotation, Point3D pocketCenter)
	{
		//Transform the frame block's location to the pocket coordinate system
		Point3D location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		//Remove frame block
		setBlockDirectly(world, location.getX(), location.getY(), location.getZ(), 0, 0);
		//Spawn Monolith
		Entity mob = new MobMonolith(world);
		mob.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), 1, 1);
		world.spawnEntityInWorld(mob);
	}
}
