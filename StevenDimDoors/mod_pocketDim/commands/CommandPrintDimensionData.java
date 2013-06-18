package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandPrintDimensionData extends DDCommandBase
{
	private static CommandPrintDimensionData instance = null;

	private CommandPrintDimensionData()
	{
		super("dd-dimensiondata");
	}

	public static CommandPrintDimensionData instance()
	{
		if (instance == null)
			instance = new CommandPrintDimensionData();

		return instance;
	}

	@Override
	protected void processCommand(EntityPlayer sender, String[] command)
	{
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
			sender.sendChatToPlayer("Error-Invalid argument, print_dim_data <targetDimID> or blank for current dim");
		}

		if(shouldGo)
		{
			if(dimHelper.dimList.containsKey(targetDim))
			{
				DimData dimData = dimHelper.dimList.get(targetDim);
				Collection<LinkData> links = new ArrayList<LinkData>();
				links.addAll( dimData.printAllLinkData());

				for (LinkData link : links)
				{
					sender.sendChatToPlayer(link.printLinkData());
				}
				sender.sendChatToPlayer("DimID= "+dimData.dimID+"Dim depth = "+dimData.depth);
			}	
		}	
	}
}
