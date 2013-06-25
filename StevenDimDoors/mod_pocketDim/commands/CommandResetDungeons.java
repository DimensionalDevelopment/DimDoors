package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandResetDungeons extends DDCommandBase
{	
	private static CommandResetDungeons instance = null;
	
	private CommandResetDungeons()
	{
		super("dd-resetdungeons", "");
	}
	
	public static CommandResetDungeons instance()
	{
		if (instance == null)
			instance = new CommandResetDungeons();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		if (command.length > 0)
		{
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		}
		
		int dungeonCount = 0;
		int resetCount = 0;
		
		for (DimData data : dimHelper.dimList.values())
		{
			if (data.isDimRandomRift)
			{
				dungeonCount++;
				if (dimHelper.instance.resetPocket(data))
				{
					resetCount++;
				}
			}
		}
		
		//Notify the user of the results
		sender.sendChatToPlayer("Reset complete. " + resetCount + " out of " + dungeonCount + " dungeons were reset.");
		return DDCommandResult.SUCCESS;
	}
}