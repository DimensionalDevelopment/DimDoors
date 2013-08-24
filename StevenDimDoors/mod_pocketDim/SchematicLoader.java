package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfig;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;

public class SchematicLoader 
{
	private SchematicLoader() { }
	
	public static boolean generateDungeonPocket(LinkData link, DDProperties properties)
	{
		if (link == null || properties == null)
		{
			return false;
		}
		try
		{
			String schematicPath;
			int originDimID = link.locDimID;
			int destDimID = link.destDimID;
			HashMap<Integer, DimData> dimList = dimHelper.dimList;
			DungeonHelper dungeonHelper = DungeonHelper.instance();
			World world;
			
			if (dimList.containsKey(destDimID))
			{
				dimList.get(destDimID).hasBeenFilled = true;
				if (dimHelper.getWorld(destDimID) == null)
				{
					dimHelper.initDimension(destDimID);
				}
				world = dimHelper.getWorld(destDimID);
				
				if (dimList.get(destDimID).dungeonGenerator == null)
				{
					//TODO: We should centralize RNG initialization and world-seed modifiers for each specific application.
					final long localSeed = world.getSeed() ^ 0x2F50DB9B4A8057E4L ^ calculateDestinationSeed(link);
					final Random random = new Random(localSeed);
					
					dungeonHelper.generateDungeonLink(link, dungeonHelper.getDimDungeonPack(originDimID), random);
				}
				schematicPath = dimList.get(destDimID).dungeonGenerator.schematicPath;
			}
			else
			{
				return false;
			}
			
			DungeonSchematic dungeon = checkSourceAndLoad(schematicPath);
			boolean valid;
			
			//Validate the dungeon's dimensions
			if (hasValidDimensions(dungeon))
			{
				dungeon.applyImportFilters(properties);
				
				//Check that the dungeon has an entrance or we'll have a crash
				if (dungeon.getEntranceDoorLocation() != null)
				{
					valid = true;
				}
				else
				{
					System.err.println("The following schematic file does not have an entrance: " + schematicPath);
					valid = false;
				}
			}
			else
			{
				System.err.println("The following schematic file has dimensions that exceed the maximum permitted dimensions for dungeons: " + schematicPath);
				valid = false;
			}
			
			if (!valid)
			{
				//TODO: In the future, remove this dungeon from the generation lists altogether.
				//That will have to wait until our code is updated to support that more easily.
				System.err.println("The dungeon will not be loaded.");
				DungeonGenerator defaultError = dungeonHelper.getDefaultErrorDungeon();
				dimList.get(destDimID).dungeonGenerator = defaultError;
				dungeon = checkSourceAndLoad(defaultError.schematicPath);
				dungeon.applyImportFilters(properties);
			}
			
			//Adjust the height at which the dungeon is placed to prevent vertical clipping
			int fixedY = adjustDestinationY(world, link.destYCoord, dungeon);
			if (fixedY != link.destYCoord)
			{
				dimHelper helperInstance = dimHelper.instance;
				helperInstance.moveLinkDataDestination(link, link.destXCoord, fixedY, link.destZCoord, link.destDimID, true);
			}
			DungeonPackConfig packConfig = dungeonHelper.getDimDungeonPack(destDimID).getConfig();
			
			dungeon.copyToWorld(world, new Point3D(link.destXCoord, link.destYCoord, link.destZCoord),
					link.linkOrientation, originDimID, destDimID, packConfig.doDistortDoorCoordinates());
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private static int adjustDestinationY(World world, int y, DungeonSchematic dungeon)
	{
		//The goal here is to guarantee that the dungeon fits within the vertical bounds
		//of the world while shifting it as little as possible.
		int destY = y;
		
		//Is the top of the dungeon going to be at Y < worldHeight?
		int entranceY = dungeon.getEntranceDoorLocation().getY();
		int pocketTop = (dungeon.getHeight() - 1) + destY - entranceY;
		int worldHeight = world.getHeight();
		if (pocketTop >= worldHeight)
		{
			destY = (worldHeight - 1) - (dungeon.getHeight() - 1) + entranceY;
		}
		
		//Is the bottom of the dungeon at Y >= 0?
		if (destY < entranceY)
		{
			destY = entranceY;
		}
		return destY;
	}

	private static boolean hasValidDimensions(DungeonSchematic dungeon)
	{
		return (dungeon.getWidth() <= DungeonHelper.MAX_DUNGEON_WIDTH &&
				dungeon.getHeight() <= DungeonHelper.MAX_DUNGEON_HEIGHT &&
				dungeon.getLength() <= DungeonHelper.MAX_DUNGEON_LENGTH);
	}
	
	private static DungeonSchematic checkSourceAndLoad(String schematicPath) throws FileNotFoundException, InvalidSchematicException
	{
		//FIXME: Change this code once we introduce an isInternal flag in dungeon data
		DungeonSchematic dungeon;
		if ((new File(schematicPath)).exists())
		{
			dungeon = DungeonSchematic.readFromFile(schematicPath);
		}
		else
		{
			dungeon = DungeonSchematic.readFromResource(schematicPath);
		}
		return dungeon;
	}
	
	private static long calculateDestinationSeed(LinkData link)
	{
		//Time for some witchcraft.
		//The code here is inspired by a discussion on Stack Overflow regarding hash codes for 3D.
		//Source: http://stackoverflow.com/questions/9858376/hashcode-for-3d-integer-coordinates-with-high-spatial-coherence
		
		//Use 8 bits from Y and 16 bits from X and Z. Mix in 8 bits from the destination dim ID too - that means
		//even if you aligned two doors perfectly between two pockets, it's unlikely they would lead to the same dungeon.
		//We map bits in reverse order to produce more varied RNG output for nearly-identical points. The reason is
		//that Java's Random outputs the 32 MSBs of its internal state to produce its output. If the differences
		//between two seeds are small (i.e. in the LSBs), then they will tend to produce similar random outputs anyway!
		
		//Only bother to assign the 48 least-significant bits since Random only takes those bits from its seed.
		//NOTE: The casts to long are necessary to get the right results from the bit shifts!!!
		
		int bit;
		int index;
		long hash;
		final int w = link.destDimID;
		final int x = link.destXCoord;
		final int y = link.destYCoord;
		final int z = link.destZCoord;
		
		hash = 0;
		index = 48;
		for (bit = 0; bit < 8; bit++)
		{
			hash |= (long) ((w >> bit) & 1) << index;
			index--;
			hash |= (long) ((x >> bit) & 1) << index;
			index--;
			hash |= (long) ((y >> bit) & 1) << index;
			index--;
			hash |= (long) ((z >> bit) & 1) << index;
			index--;
		}
		for (; bit < 16; bit++)
		{
			hash |= (long) ((x >> bit) & 1) << index;
			index--;
			hash |= (long) ((z >> bit) & 1) << index;
			index--;
		}
		
		return hash;
	}
}