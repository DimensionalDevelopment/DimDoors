package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class CommandDeleteRifts extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "delete_rifts";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		int linksRemoved=0;
		int targetDim;
		boolean shouldGo= true;
		
		if(var2.length==0)
		{
			targetDim= this.getCommandSenderAsPlayer(var1).worldObj.provider.dimensionId;
		}
		else if(var2.length==1)
		{
			targetDim= this.parseInt(var1, var2[0]);
			if(!dimHelper.dimList.containsKey(targetDim))
			{
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Error- dim "+targetDim+" not registered");
				shouldGo=false;

			}
		}
		else
		{
			targetDim=0;
			shouldGo=false;
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Error-Invalid argument, delete_links <targetDimID> or blank for current dim");

		
		}
		
		
		
		
		if(shouldGo)
		{
			if(dimHelper.dimList.containsKey(targetDim))
			{
				DimData dim = dimHelper.dimList.get(targetDim);
			
				ArrayList<LinkData> linksInDim = dim.printAllLinkData();
				
				for(LinkData link : linksInDim)
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
					
					if(targetWorld.getBlockId(link.locXCoord, link.locYCoord, link.locZCoord)==mod_pocketDim.blockRiftID)
					{
						dim.removeLinkAtCoords(link);
					
					
					
						targetWorld.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, 0);
					
					
						linksRemoved++;
					}
				
				}
				
				//dim.linksInThisDim.clear();
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+linksRemoved+" rifts.");
				
			}
			
		}
		
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2.length));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+linksRemoved+" rifts.");

		
	// TODO Auto-generated method stub
	
	}
}