package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

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
		DimData dimData;

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
		
		dimData = dimHelper.dimList.get(targetDim);
		if (dimData == null)
		{
			return DDCommandResult.UNREGISTERED_DIMENSION;
		}

		ArrayList<LinkData> links = dimData.printAllLinkData();

		sender.sendChatToPlayer("Dimension ID = " + dimData.dimID);
		sender.sendChatToPlayer("Dimension Depth = " + dimData.depth);
		for (LinkData link : links)
		{
			sender.sendChatToPlayer(link.printLinkData());
		}
		return DDCommandResult.SUCCESS;
	}
}
