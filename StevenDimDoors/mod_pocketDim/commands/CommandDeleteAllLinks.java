package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandDeleteAllLinks extends DDCommandBase
{
	private static CommandDeleteAllLinks instance = null;

	private CommandDeleteAllLinks()
	{
		super("dd-deletelinks", "FIXME");
	}

	public static CommandDeleteAllLinks instance()
	{
		if (instance == null)
			instance = new CommandDeleteAllLinks();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		int linksRemoved=0;
		int targetDim;
		boolean shouldGo= true;

		if(command.length==0)
		{
			targetDim= sender.worldObj.provider.dimensionId;
		}
		else if(command.length==1)
		{
			targetDim = parseInt(sender, command[0]);
			if(!dimHelper.dimList.containsKey(targetDim))
			{
				sender.sendChatToPlayer("Error- dim "+targetDim+" not registered");
				shouldGo=false;
			}
		}
		else
		{
			targetDim=0;
			shouldGo=false;
			sender.sendChatToPlayer("Error-Invalid argument, delete_all_links <targetDimID> or blank for current dim");
		}

		if(shouldGo)
		{
			if(dimHelper.dimList.containsKey(targetDim))
			{
				DimData dim = dimHelper.dimList.get(targetDim);
				ArrayList<LinkData> linksInDim = dim.printAllLinkData();

				for (LinkData link : linksInDim)
				{
					World targetWorld = dimHelper.getWorld(targetDim);

					if(targetWorld==null)
					{
						dimHelper.initDimension(targetDim);
					}
					else if(targetWorld.provider==null)
					{
						dimHelper.initDimension(targetDim);
					}
					targetWorld = dimHelper.getWorld(targetDim);
					dim.removeLinkAtCoords(link);
					targetWorld.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, 0);
					linksRemoved++;
				}
				//dim.linksInThisDim.clear();
				sender.sendChatToPlayer("Removed " + linksRemoved + " links.");
			}
		}
		return DDCommandResult.SUCCESS; //TEMPORARY HACK
	}
}