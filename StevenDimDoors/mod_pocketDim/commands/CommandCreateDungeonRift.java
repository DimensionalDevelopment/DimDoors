package StevenDimDoors.mod_pocketDim.commands;

import java.io.File;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandCreateDungeonRift extends DDCommandBase
{
	private static CommandCreateDungeonRift instance = null;
	
	private CommandCreateDungeonRift()
	{
		super("dd-rift", "<dungeon name | 'list' | 'random'>");
	}
	
	public static CommandCreateDungeonRift instance()
	{
		if (instance == null)
			instance = new CommandCreateDungeonRift();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		if (sender.worldObj.isRemote)
		{
			return DDCommandResult.SUCCESS;
		}		
		if (command.length == 0)
		{
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		}
		if (command.length > 1)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		
		if (command[0].equals("list"))
		{
			Collection<String> dungeonNames = dungeonHelper.getDungeonNames();
			for (String name : dungeonNames)
			{
				sender.sendChatToPlayer(name);
			}
			sender.sendChatToPlayer("");
		}
		else
		{
			DungeonGenerator result;
			int x = (int) sender.posX;
			int y = (int) sender.posY;
			int z = (int) sender.posZ;
			LinkData link = new LinkData(sender.worldObj.provider.dimensionId, 0, x, y + 1, z, x, y + 1, z, true, 3);
			
			if (command[0].equals("random"))
			{
				dimHelper.instance.createLink(link);
				link = dimHelper.instance.createPocket(link, true, true);
				sender.sendChatToPlayer("Created a rift to a random dungeon (Dimension ID = " + link.destDimID + ").");
			}
			else
			{
				result = findDungeonByPartialName(command[0], dungeonHelper.registeredDungeons);
				if (result == null)
				{
					result = findDungeonByPartialName(command[0], dungeonHelper.customDungeons);
				}
				//Check if we found any matches
				if (result != null)
				{
					//Create a rift to our selected dungeon and notify the player
					link = dimHelper.instance.createPocket(link, true, true);
					dimHelper.dimList.get(link.destDimID).dungeonGenerator = result;
					sender.sendChatToPlayer("Created a rift to \"" + getSchematicName(result) + "\" dungeon (Dimension ID = " + link.destDimID + ").");
				}
				else
				{
					//No matches!
					return new DDCommandResult("Error: The specified dungeon was not found. Use 'list' to see a list of the available dungeons.");
				}
			}
		}
		return DDCommandResult.SUCCESS;
	}
	
	private DungeonGenerator findDungeonByPartialName(String query, Collection<DungeonGenerator> dungeons)
	{
		//Search for the shortest dungeon name that contains the lowercase query string.
		String dungeonName;
		String normalQuery = query.toLowerCase();
		DungeonGenerator bestMatch = null;
		int matchLength = Integer.MAX_VALUE;
		
		for (DungeonGenerator dungeon : dungeons)
		{
			//We need to extract the file's name. Comparing against schematicPath could
			//yield false matches if the query string is contained within the path.
			
			dungeonName = getSchematicName(dungeon).toLowerCase();
			if (dungeonName.length() < matchLength && dungeonName.contains(normalQuery))
			{
				matchLength = dungeonName.length();
				bestMatch = dungeon;
			}
		}
		return bestMatch;
	}
	
	private static String getSchematicName(DungeonGenerator dungeon)
	{
		//TODO: Move this to DungeonHelper and use it for all schematic name parsing.
		//In the future, we really should include the schematic's name as part of DungeonGenerator
		//to avoid redoing this work constantly.
		File schematic = new File(dungeon.schematicPath);
		String fileName = schematic.getName();
		return fileName.substring(0, fileName.length() - DungeonHelper.SCHEMATIC_FILE_EXTENSION.length());
	}
}