package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

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
		ArrayList<Integer> dimIdsToDelete= new ArrayList<Integer>();
		
		for (NewDimData data : PocketManager.getDimensions())
		{
			dungeonCount++;
			if(DimensionManager.getWorld(data.id())==null)
			{
				if (data.isDungeon())
				{
					PocketManager.deleteDimensionFolder(data);
					dimIdsToDelete.add(data.id());
				}
			}
			else
			{
				for(DimLink link : data.links())
				{
					if(link.linkType()==LinkTypes.REVERSE)
					{
						data.createLink(link.source(), LinkTypes.DUNGEON_EXIT, link.orientation());
					}
					if(link.linkType()==LinkTypes.DUNGEON)
					{
						data.createLink(link.source(), LinkTypes.DUNGEON, link.orientation());
					}
				}
			}
		}
		resetCount = PocketManager.deleteDimensionData(dimIdsToDelete);
		//TODO implement blackList
		
		//Notify the user of the results
		sender.sendChatToPlayer("Reset complete. " + resetCount + " out of " + dungeonCount + " dungeons were reset.");
		return DDCommandResult.SUCCESS;
	}
}