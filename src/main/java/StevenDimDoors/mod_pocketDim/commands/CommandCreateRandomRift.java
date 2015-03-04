package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;

public class CommandCreateRandomRift extends DDCommandBase
{
	private static CommandCreateRandomRift instance = null;
	private static Random random = new Random();

	private CommandCreateRandomRift()
	{
		super("dd-random", "<dungeon name>");
	}

	public static CommandCreateRandomRift instance()
	{
		if (instance == null)
			instance = new CommandCreateRandomRift();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		NewDimData dimension;
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		if (command.length > 1)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		
		DimLink link;
		DungeonData result;
		int x = MathHelper.floor_double(sender.posX);
		int y = MathHelper.floor_double(sender.posY);
		int z = MathHelper.floor_double (sender.posZ);
		int orientation = MathHelper.floor_double((sender.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;

		if (command.length == 0)
		{
			dimension = PocketManager.getDimensionData(sender.worldObj);
			link = dimension.createLink(x, y + 1, z, LinkType.DUNGEON, orientation);

			sender.worldObj.setBlock(x, y + 1, z,mod_pocketDim.blockRift, 0, 3);
			sendChat(sender, "Created a rift to a random dungeon.");
		}
		else
		{
			result = getRandomDungeonByPartialName(command[0], dungeonHelper.getRegisteredDungeons());
			if (result == null)
			{
				result = getRandomDungeonByPartialName(command[0], dungeonHelper.getUntaggedDungeons());
			}
			
			// Check if we found any matches
			if (result != null)
			{
				dimension = PocketManager.getDimensionData(sender.worldObj);
				link = dimension.createLink(x, y + 1, z, LinkType.DUNGEON, orientation);

				if (PocketBuilder.generateSelectedDungeonPocket(link, mod_pocketDim.properties, result))
				{
					// Create a rift to our selected dungeon and notify the player
					sender.worldObj.setBlock(x, y + 1, z, mod_pocketDim.blockRift, 0, 3);
					sendChat(sender, "Created a rift to \"" + result.schematicName() + "\" dungeon (Dimension ID = " + link.destination().getDimension() + ").");
				}
				else
				{
					// Dungeon generation failed somehow. Notify the user and remove the useless link.
					dimension.deleteLink(link);
					sendChat(sender, "Dungeon generation failed unexpectedly!");
				}
			}
			else
			{
				//No matches!
				return new DDCommandResult("Error: The specified dungeon was not found. Use 'list' to see a list of the available dungeons.");
			}
		}
		return DDCommandResult.SUCCESS;
	}

	private static DungeonData getRandomDungeonByPartialName(String query, Collection<DungeonData> dungeons)
	{
		// Search for all dungeons that contain the lowercase query string.
		String dungeonName;
		String normalQuery = query.toLowerCase();
		ArrayList<DungeonData> matches = new ArrayList<DungeonData>();

		for (DungeonData dungeon : dungeons)
		{
			// We need to extract the file's name. Comparing against schematicPath could
			// yield false matches if the query string is contained within the path.
			dungeonName = dungeon.schematicName().toLowerCase();
			if (dungeonName.contains(normalQuery))
			{
				matches.add(dungeon);
			}
		}
		if (matches.isEmpty())
		{
			return null;
		}
		return matches.get( random.nextInt(matches.size()) );
	}
}