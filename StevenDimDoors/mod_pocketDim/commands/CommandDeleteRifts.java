package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
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

		if(command.length==1)
		{
			targetDim = parseInt(sender, command[0]);
		}
		else
		{
			targetDim=0;
			shouldGo=false;
			sendChat(sender,("Error-Invalid argument, delete_all_links <targetDimID>"));
		}

		if(shouldGo)
		{
			
				NewDimData dim = PocketManager.getDimensionData(targetDim);
				ArrayList<DimLink> linksInDim = dim.getAllLinks();

				for (DimLink link : linksInDim)
				{
					World targetWorld = PocketManager.loadDimension(targetDim);
				
					if(sender.worldObj.getBlockId(link.source().getX(), link.source().getY(), link.source().getZ())==mod_pocketDim.blockRift.blockID)
					{
						targetWorld.setBlock(link.source().getX(), link.source().getY(), link.source().getZ(), 0);
						linksRemoved++;
						dim.deleteLink(link);
					}
				}
				sendChat(sender,("Removed " + linksRemoved + " rifts."));
			
		}
		return DDCommandResult.SUCCESS; //TEMPORARY HACK
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return null;
	}
}