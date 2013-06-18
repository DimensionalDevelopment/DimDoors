package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandPruneDimensions extends DDCommandBase
{
	private static CommandPruneDimensions instance = null;
	
	private CommandPruneDimensions()
	{
		super("dd-prune");
	}
	
	public static CommandPruneDimensions instance()
	{
		if (instance == null)
			instance = new CommandPruneDimensions();
		
		return instance;
	}

	@Override
	protected void processCommand(EntityPlayer sender, String[] command)
	{
		int numRemoved=0;
		ArrayList<Integer> dimsWithLinks = new ArrayList<Integer>();
		Collection<DimData> allDims = new ArrayList<DimData>(); 
		allDims.addAll(dimHelper.dimList.values());
		for(DimData data: allDims)
		{
			for(LinkData link:data.printAllLinkData())
			{
				if(!dimsWithLinks.contains(link.destDimID))
				{
					dimsWithLinks.add(link.destDimID);
				}
			}
		}
		for(LinkData link : dimHelper.instance.interDimLinkList.values())
		{
			if(!dimsWithLinks.contains(link.destDimID))
			{
				dimsWithLinks.add(link.destDimID);
			}
		}
		for(DimData data : allDims)
		{
			if(!dimsWithLinks.contains(data.dimID))
			{
				dimHelper.dimList.remove(data.dimID);
				numRemoved++;
			}
		}
		dimHelper.instance.save();
		sender.sendChatToPlayer("Removed " + numRemoved + " unreachable pocket dims.");
	}
}