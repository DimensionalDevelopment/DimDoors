package StevenDimDoors.mod_pocketDim.commands;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandRegenPocket extends DDCommandBase
{	
	private static CommandRegenPocket instance = null;
	
	private CommandRegenPocket()
	{
		super("dd-regenDungeons");
	}
	
	public static CommandRegenPocket instance()
	{
		if (instance == null)
			instance = new CommandRegenPocket();
		
		return instance;
	}

	@Override
	protected void processCommand(EntityPlayer sender, String[] command)
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		DDProperties properties = DDProperties.instance();
		
		for(DimData data : dimHelper.dimList.values())
		{
			if(data.isDimRandomRift)
			{
				dimHelper.instance.regenPocket(data);
			}
		}
		
	}
}