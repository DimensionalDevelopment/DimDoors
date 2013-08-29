package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.ILinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class CommandDeleteDimensionData extends DDCommandBase
{
	private static CommandDeleteDimensionData instance = null;
	
	private CommandDeleteDimensionData()
	{
		super("dd-deletedimension", "???");
	}
	
	public static CommandDeleteDimensionData instance()
	{
		if (instance == null)
			instance = new CommandDeleteDimensionData();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		int linksRemoved=0;
		int targetDim;
		boolean shouldGo= true;
		
		if (command.length==0)
		{
			targetDim= sender.worldObj.provider.dimensionId;
		}
		else if (command.length==1)
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
			sender.sendChatToPlayer("Error-Invalid argument, delete_dim_data <targetDimID> or blank for current dim");
		}
		
		if(shouldGo)
		{
			if(PocketManager.dimList.containsKey(targetDim))
			{
				try
				{
					for(NewDimData newDimData :PocketManager.dimList.values())
					{
						Collection<ILinkData> links= new ArrayList<ILinkData>();
						links.addAll( newDimData.getLinksInDim());
					
						for(ILinkData link : links)
						{
							if(link.destDimID==targetDim)
							{
								PocketManager.instance.getDimData(link.locDimID).removeLinkAtCoords(link);
								linksRemoved++;
							}
							if(newDimData.dimID==targetDim)
							{
								linksRemoved++;
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				PocketManager.dimList.remove(targetDim);
				sender.sendChatToPlayer("Removed dimension " + targetDim + " from DimDoors and deleted " + linksRemoved + " links");
			}
			else
			{
				sender.sendChatToPlayer("Error- dimension "+targetDim+" not registered with dimDoors");
			}	
		}
		return DDCommandResult.SUCCESS; //TEMPORARY HACK
	}
}