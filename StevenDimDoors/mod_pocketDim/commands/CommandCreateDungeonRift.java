package StevenDimDoors.mod_pocketDim.commands;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

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
		NewDimData dimension;
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
			DimLink link;
			DungeonData result;
			int x = MathHelper.floor_double(sender.posX);
			int y = MathHelper.floor_double(sender.posY);
			int z = MathHelper.floor_double (sender.posZ);
			int orientation = MathHelper.floor_double((double) ((sender.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

			if (command[0].equals("random"))
			{
				
				dimension = PocketManager.getDimensionData(sender.worldObj);
				link = dimension.createLink(x, y + 1, z, LinkTypes.DUNGEON);
				sender.worldObj.setBlock(x, y + 1, z,mod_pocketDim.blockRift.blockID,0,3);

				sender.sendChatToPlayer("Created a rift to a random dungeon.");
			}
			else
			{
				result = findDungeonByPartialName(command[0], dungeonHelper.getRegisteredDungeons());
				if (result == null)
				{
					result = findDungeonByPartialName(command[0], dungeonHelper.getUntaggedDungeons());
				}
				//Check if we found any matches
				if (result != null)
				{
					//Create a rift to our selected dungeon and notify the player
					//TODO currently crashes
					dimension = PocketManager.getDimensionData(sender.worldObj);
					link = dimension.createLink(x, y + 1, z, LinkTypes.DUNGEON);
					PocketManager.getDimensionData(link.destination().getDimension()).initializeDungeon(x, y + 1, z, orientation,link, result);
					sender.worldObj.setBlock(x, y + 1, z,mod_pocketDim.blockRift.blockID,0,3);
					sender.sendChatToPlayer("Created a rift to \"" + result.schematicName() + "\" dungeon (Dimension ID = " + link.destination().getDimension() + ").");
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
	
	private DungeonData findDungeonByPartialName(String query, Collection<DungeonData> dungeons)
	{
		//Search for the shortest dungeon name that contains the lowercase query string.
		String dungeonName;
		String normalQuery = query.toLowerCase();
		DungeonData bestMatch = null;
		int matchLength = Integer.MAX_VALUE;
		
		for (DungeonData dungeon : dungeons)
		{
			//We need to extract the file's name. Comparing against schematicPath could
			//yield false matches if the query string is contained within the path.
			
			dungeonName = dungeon.schematicName().toLowerCase();
			if (dungeonName.length() < matchLength && dungeonName.contains(normalQuery))
			{
				matchLength = dungeonName.length();
				bestMatch = dungeon;
			}
		}
		return bestMatch;
	}
}