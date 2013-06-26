package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

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
		Collection<DimData> allDims = new ArrayList<DimData>(); 
		allDims.addAll(dimHelper.dimList.values());
		
		for (DimData data : allDims)
		{
			for (LinkData link : data.printAllLinkData())
			{
				linkedDimensions.add(link.destDimID);
			}
		}
		for (LinkData link : dimHelper.instance.interDimLinkList.values())
		{
			linkedDimensions.add(link.destDimID);
		}
		for (DimData data : allDims)
		{
			if (!linkedDimensions.contains(data.dimID))
			{
				if (dimHelper.instance.pruneDimension(data, deleteFolders))
				{
					removedCount++;
				}
			}
		}
		dimHelper.instance.save();
		sender.sendChatToPlayer("Removed " + removedCount + " unreachable pocket dims.");
		return DDCommandResult.SUCCESS;
	}
}