package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileNotFoundException;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;

public class SchematicLoader 
{	
	private SchematicLoader() { }
	
	public static void generateDungeonPocket(LinkData link, DDProperties properties)
	{
		//TODO: Phase this function out in the next update. ~SenseiKiwi
		
		String filePath=DungeonHelper.instance().defaultBreak.schematicPath;
		if(dimHelper.dimList.containsKey(link.destDimID))
		{
			if(dimHelper.dimList.get(link.destDimID).dungeonGenerator == null)
			{
				DungeonHelper.instance().generateDungeonLink(link);
			}
			filePath = dimHelper.dimList.get(link.destDimID).dungeonGenerator.schematicPath;	
		}
		
		//this.generateSchematic(link.destXCoord, link.destYCoord, link.destZCoord, link.linkOrientation, link.destDimID, link.locDimID, filePath);
		
		try
		{
			int originDimID = link.locDimID;
			int destDimID = link.destDimID;
			DungeonSchematic dungeon;
			if ((new File(filePath)).exists())
			{
				dungeon = DungeonSchematic.readFromFile(filePath);
			}
			else
			{
				dungeon = DungeonSchematic.readFromResource(filePath);
			}
			dungeon.applyImportFilters(properties);
			
			dimHelper.dimList.get(destDimID).hasBeenFilled = true;
			if (dimHelper.getWorld(destDimID) == null)
			{
				dimHelper.initDimension(destDimID);
			}
			World world = dimHelper.getWorld(destDimID);
			
			dungeon.copyToWorld(world, new Point3D(link.destXCoord, link.destYCoord, link.destZCoord), link.linkOrientation, originDimID, destDimID);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InvalidSchematicException e)
		{
			e.printStackTrace();
		}
	}
}