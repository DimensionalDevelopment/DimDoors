package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.ILinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class CommandPrintDimensionData extends DDCommandBase
{
	private static CommandPrintDimensionData instance = null;

	private CommandPrintDimensionData()
	{
		super("dd-dimensiondata", "[dimension number]");
	}

	public static CommandPrintDimensionData instance()
	{
		if (instance == null)
			instance = new CommandPrintDimensionData();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		int targetDim;
		NewDimData newDimData;

		if (command.length == 0)
		{
			targetDim = sender.worldObj.provider.dimensionId;
		}
		else if (command.length == 1)
		{
			try
			{
				targetDim = Integer.parseInt(command[0]);
			}
			catch (Exception ex)
			{
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
		}
		else
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		
		newDimData = PocketManager.instance.getDimData(targetDim);
		if (newDimData == null)
		{
			return DDCommandResult.UNREGISTERED_DIMENSION;
		}

		ArrayList<ILinkData> links = newDimData.getLinksInDim();

		sender.sendChatToPlayer("Dimension ID = " + newDimData.dimID);
		sender.sendChatToPlayer("Dimension Depth = " + newDimData.depth);
		for (ILinkData link : links)
		{
			sender.sendChatToPlayer(link.printLinkData());
		}
		return DDCommandResult.SUCCESS;
	}
}
