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
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.schematic.CompoundFilter;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;
import StevenDimDoors.mod_pocketDim.schematic.ReplacementFilter;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.util.Point4D;

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
		return entranceDoorLocation.clone();
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
		
		//Flip the entrance's orientation to get the dungeon's orientation
		orientation = BlockRotator.transformMetadata(finder.getEntranceOrientation(), 2, Block.doorWood.blockID);

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

	public void copyToWorld(World world, Point3D pocketCenter, int dungeonOrientation, IDimLink entryLink, Random random)
	{
		//TODO: This function is an improvised solution so we can get the release moving. In the future,
		//we should generalize block transformations and implement support for them at the level of Schematic,
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
					blockMeta = BlockRotator.transformMetadata(metadata[index], turnAngle, blockID);

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
		
		setUpDungeon(PocketManager.getDimensionData(world), world, pocketCenter, turnAngle, entryLink, random);
	}
	
	private void setUpDungeon(NewDimData dimension, World world, Point3D pocketCenter, int turnAngle, IDimLink entryLink, Random random)
	{
        //Transform dungeon corners
        Point3D minCorner = new Point3D(0, 0, 0);
        Point3D maxCorner = new Point3D(width - 1, height - 1, length - 1);
        transformCorners(entranceDoorLocation, pocketCenter, turnAngle, minCorner, maxCorner);
        
		//Fill empty chests and dispensers
		FillContainersOperation filler = new FillContainersOperation(random);
		filler.apply(world, minCorner, maxCorner);
		
		//Set up entrance door rift
		createEntranceReverseLink(dimension, pocketCenter, entryLink);
		
		//Set up link data for dimensional doors
		for (Point3D location : dimensionalDoorLocations)
		{
			createDimensionalDoorLink(dimension, location, entranceDoorLocation, turnAngle, pocketCenter);
		}
		
		//Set up link data for exit door
		for (Point3D location : exitDoorLocations)
		{
			createExitDoorLink(dimension, location, entranceDoorLocation, turnAngle, pocketCenter);
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
	
	private static void createEntranceReverseLink(NewDimData dimension, Point3D pocketCenter, IDimLink entryLink)
	{
		IDimLink link = dimension.createLink(pocketCenter.getX(), pocketCenter.getY(), pocketCenter.getZ(), IDimLink.TYPE_NORMAL);
		Point4D destination = link.source();
		NewDimData prevDim = PocketManager.getDimensionData(destination.getDimension());
		prevDim.setDestination(link, destination.getX(), destination.getY(), destination.getZ());
	}
	
	private static void createExitDoorLink(NewDimData dimension, Point3D point, Point3D entrance, int rotation, Point3D pocketCenter)
	{
		//Transform the door's location to the pocket coordinate system
		Point3D location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		dimension.createLink(location.getX(), location.getY(), location.getZ(), IDimLink.TYPE_DUNGEON_EXIT);
	}
	
	private static void createDimensionalDoorLink(NewDimData dimension, Point3D point, Point3D entrance, int rotation, Point3D pocketCenter)
	{
		//Transform the door's location to the pocket coordinate system
		Point3D location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		dimension.createLink(location.getX(), location.getY(), location.getZ(), IDimLink.TYPE_DUNGEON);
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
