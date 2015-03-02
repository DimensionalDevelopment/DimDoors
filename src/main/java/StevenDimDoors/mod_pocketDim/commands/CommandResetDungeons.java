package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class CommandResetDungeons extends DDCommandBase
{	
	private static CommandResetDungeons instance = null;
	
	private CommandResetDungeons()
	{
		super("dd-resetdungeons", "");
	}
	
	public static CommandResetDungeons instance()
	{
		if (instance == null)
			instance = new CommandResetDungeons();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		if (command.length > 0)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}

		int id;
		int resetCount = 0;
		int dungeonCount = 0;
		HashSet<Integer> deletedDimensions = new HashSet<Integer>();
		ArrayList<NewDimData> loadedDungeons = new ArrayList<NewDimData>();

		// Copy the list of dimensions to iterate over the copy. Otherwise,
		// we would trigger an exception by modifying the original list.
		ArrayList<NewDimData> dimensions = new ArrayList<NewDimData>();
		for (NewDimData dimension : PocketManager.getDimensions())
		{

			dimensions.add(dimension);
		}
		
		// Iterate over the list of dimensions. Check which ones are dungeons.
		// If a dungeon is found, try to delete it. If it can't be deleted,
		// then it must be loaded and needs to be updated to prevent bugs.
		for (NewDimData dimension : dimensions)
		{
			if (dimension.type() == DimensionType.DUNGEON)
			{
				dungeonCount++;
				id = dimension.id();
				if (PocketManager.deletePocket(dimension, true))
				{
					resetCount++;
					deletedDimensions.add(id);
				}
				else
				{
					loadedDungeons.add(dimension);
				}
			}

		}
		
		// Modify the loaded dungeons to prevent bugs
		for (NewDimData dungeon : loadedDungeons)
		{
			// Find top-most loaded dungeons and update their parents.
			// They will automatically update their children.
			// Dungeons with non-dungeon parents don't need to be fixed.
			if (dungeon.parent() == null)
			{
				dungeon.setParentToRoot();
			}
			
			// Links to any deleted dungeons must be replaced
			for (DimLink link : dungeon.links())
			{
				if (link.hasDestination() && deletedDimensions.contains(link.destination().getDimension()))
				{

					if (link.linkType() == LinkType.DUNGEON)
					{
						dungeon.createLink(link.source(), LinkType.DUNGEON, link.orientation(), null);
					}
					else if (link.linkType() == LinkType.REVERSE)
					{
						dungeon.createLink(link.source(), LinkType.DUNGEON_EXIT, link.orientation(), null);
					}
				}
			}
		}
		
		// Notify the user of the results
		sendChat(sender,("Reset complete. " + resetCount + " out of " + dungeonCount + " dungeons were reset."));
		return DDCommandResult.SUCCESS;
	}
}