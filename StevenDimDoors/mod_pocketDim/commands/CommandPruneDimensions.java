package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.ILinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class CommandPruneDimensions extends DDCommandBase
{
	private static CommandPruneDimensions instance = null;
	
	private CommandPruneDimensions()
	{
		super("dd-prune", "['delete']");
	}
	
	public static CommandPruneDimensions instance()
	{
		if (instance == null)
			instance = new CommandPruneDimensions();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		if (command.length > 1)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		if (command.length == 1 && !command[0].equalsIgnoreCase("delete"))
		{
			return DDCommandResult.INVALID_ARGUMENTS;
		}
		
		int removedCount = 0;
		boolean deleteFolders = (command.length == 1);
		Set<Integer> linkedDimensions = new HashSet<Integer>();
		Collection<NewDimData> allDims = new ArrayList<NewDimData>(); 
		allDims.addAll(PocketManager.dimList.values());
		
		for (NewDimData data : allDims)
		{
			for (ILinkData link : data.getLinksInDim())
			{
				linkedDimensions.add(link.destDimID);
			}
		}
		for (ILinkData link : dimHelper.PocketManager.interDimLinkList.values())
		{
			linkedDimensions.add(link.destDimID);
		}
		for (NewDimData data : allDims)
		{
			if (!linkedDimensions.contains(data.dimID))
			{
				if (PocketManager.instance.pruneDimension(data, deleteFolders))
				{
					removedCount++;
				}
			}
		}
		PocketManager.instance.save();
		sender.sendChatToPlayer("Removed " + removedCount + " unreachable pocket dims.");
		return DDCommandResult.SUCCESS;
	}
}