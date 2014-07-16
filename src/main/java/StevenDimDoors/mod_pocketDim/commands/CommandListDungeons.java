package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

public class CommandListDungeons extends DDCommandBase
{
	private static CommandListDungeons instance = null;

	private CommandListDungeons()
	{
		super("dd-list", "<page>");
	}

	public static CommandListDungeons instance()
	{
		if (instance == null)
			instance = new CommandListDungeons();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		int page;
		int index;
		int limit;
		int pageCount;
		ArrayList<String> dungeonNames;

		if (command.length > 1)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		if (command.length == 0)
		{
			page = 1;
		}
		else
		{
			try
			{
				page = Integer.parseInt(command[0]);
			}
			catch (NumberFormatException e) 
			{
				return DDCommandResult.INVALID_ARGUMENTS;
			}
		}
		dungeonNames = DungeonHelper.instance().getDungeonNames();
		pageCount = (dungeonNames.size() - 1) / 10 + 1;
		if (page < 1 || page > pageCount)
		{
			return DDCommandResult.INVALID_ARGUMENTS;
		}
		sendChat(sender, "List of dungeons (page " + page + " of " + pageCount + "):");
		index = (page - 1) * 10;
		limit = Math.min(index + 10, dungeonNames.size());
		for (; index < limit; index++)
		{
			sendChat(sender, dungeonNames.get(index));
		}
		sendChat(sender, "");
		
		return DDCommandResult.SUCCESS;
	}
}