package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class CommandPrintDimData extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "print_dim_data";
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
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Error-Invalid argument, print_dim_data <targetDimID> or blank for current dim");

		
		}
		
		
		
		
		if(shouldGo)
		{
			if(dimHelper.dimList.containsKey(targetDim))
			{
				for(DimData dimData :dimHelper.dimList.values())
				{
					Collection<LinkData> links= new ArrayList();
					links.addAll( dimData.printAllLinkData());
					
					for(LinkData link : links)
					{
						if(link.destDimID==targetDim)
						{
							dimHelper.dimList.get(link.locDimID).removeLinkAtCoords(link);
							dimHelper.instance.linksForRendering.remove(link);
							linksRemoved++;
						}
						if(dimData.dimID==targetDim)
						{
							dimHelper.instance.linksForRendering.remove(link);
							linksRemoved++;
						}
					}
					
					
				}
				
				dimHelper.dimList.remove(targetDim);
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed dimension "+targetDim+" from DimDoors and deleted "+linksRemoved+" links");
				
			}
			else
			{
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Error- dimension "+targetDim+" not registered with dimDoors");
			}
			
		}
		
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2.length));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+linksRemoved+" rifts.");

		
	// TODO Auto-generated method stub
	
	}
}