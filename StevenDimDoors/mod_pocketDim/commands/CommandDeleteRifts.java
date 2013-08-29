package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.ILinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class CommandDeleteRifts extends DDCommandBase
{
	private static CommandDeleteRifts instance = null;

	private CommandDeleteRifts()
	{
		super("dd-???", "???");
	}

	public static CommandDeleteRifts instance()
	{
		if (instance == null)
			instance = new CommandDeleteRifts();

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
			if(!PocketManager.dimList.containsKey(targetDim))
			{
				sender.sendChatToPlayer("Error- dim "+targetDim+" not registered");
				shouldGo=false;
			}
		}
		else
		{
			targetDim=0;
			shouldGo=false;
			sender.sendChatToPlayer("Error-Invalid argument, delete_links <targetDimID> or blank for current dim");
		}

		if(shouldGo)
		{
			if(PocketManager.dimList.containsKey(targetDim))
			{
				NewDimData dim = PocketManager.instance.getDimData(targetDim);
				ArrayList<ILinkData> linksInDim = dim.getLinksInDim();

				for(ILinkData link : linksInDim)
				{
					World targetWorld = PocketManager.getWorld(targetDim);

					if(targetWorld==null)
					{
						PocketManager.initDimension(targetDim);
					}
					else if(targetWorld.provider==null)
					{
						PocketManager.initDimension(targetDim);
					}
					targetWorld = PocketManager.getWorld(targetDim);

					if (targetWorld.getBlockId(link.locXCoord, link.locYCoord, link.locZCoord) == mod_pocketDim.blockRift.blockID)
					{
						dim.removeLinkAtCoords(link);
						targetWorld.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, 0);
						linksRemoved++;
					}
				}
				sender.sendChatToPlayer("Removed "+linksRemoved+" rifts.");	
			}	
		}
		return DDCommandResult.SUCCESS; //TEMPORARY HACK
	}
}