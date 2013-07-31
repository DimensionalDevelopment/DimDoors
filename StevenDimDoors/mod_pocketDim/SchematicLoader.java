package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;

public class SchematicLoader 
{
	private SchematicLoader() { }
	
	public static boolean generateDungeonPocket(LinkData link, DDProperties properties)
	{
		//TODO: Phase this function out in the next update. ~SenseiKiwi
		
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
			
			if (dimList.containsKey(destDimID))
			{
				if (dimList.get(destDimID).dungeonGenerator == null)
				{
					DungeonHelper.instance().generateDungeonLink(link);
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
				dungeon = checkSourceAndLoad(DungeonHelper.instance().defaultBreak.schematicPath);
				dungeon.applyImportFilters(properties);
			}
			
			dimList.get(destDimID).hasBeenFilled = true;
			if (dimHelper.getWorld(destDimID) == null)
			{
				dimHelper.initDimension(destDimID);
			}
			World world = dimHelper.getWorld(destDimID);
			
			//Adjust the height at which the dungeon is placed to prevent vertical clipping
			//link.destYCoord = adjustDestinationY(world, link.destYCoord, dungeon);
			
			//Adjust the data for the destination link to prevent crashes
			//TODO: I really have no idea how to do this. =/ The LinkData/dimHelper implementation causes me sadness. ~SenseiKiwi
			
			dungeon.copyToWorld(world, new Point3D(link.destXCoord, link.destYCoord, link.destZCoord), link.linkOrientation, originDimID, destDimID);
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
		//TODO: Change this code once we introduce an isInternal flag in dungeon data
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
}