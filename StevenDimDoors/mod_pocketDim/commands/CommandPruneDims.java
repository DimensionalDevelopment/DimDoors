package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandPruneDims extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "prune_pocket_dims";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		int numRemoved=0;
		ArrayList dimsWithLinks=new ArrayList();
		Collection<DimData> allDims = new ArrayList(); 
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
		this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+numRemoved+" unreachable pocket dims.");

		
			
		
		
			
		
	
	}
}