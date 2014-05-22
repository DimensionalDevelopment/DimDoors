package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

@SuppressWarnings("deprecation")
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
		if(sender.worldObj.isRemote)
		{
			return DDCommandResult.SUCCESS; 
		}
		if (command.length > 0)
		{
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		}
		
		int dungeonCount = 0;
		int resetCount = 0;
		ArrayList<Integer> dimsToDelete = new ArrayList<Integer>();
		ArrayList<Integer> dimsToFix = new ArrayList<Integer>();

		for (NewDimData data : PocketManager.getDimensions())
		{
			
			if(DimensionManager.getWorld(data.id())==null&&data.isDungeon())
			{
				resetCount++;
				dungeonCount++;
				dimsToDelete.add(data.id());
			}
			else if(data.isDungeon())
			{
				dimsToFix.add(data.id());
				dungeonCount++;
				for(DimLink link : data.links())
				{
					if(link.linkType()==LinkTypes.REVERSE)
					{
						data.createLink(link.source(), LinkTypes.DUNGEON_EXIT, link.orientation(), null);
					}
					if(link.linkType()==LinkTypes.DUNGEON)
					{
						data.createLink(link.source(), LinkTypes.DUNGEON, link.orientation(), null);
					}
				}
			}
		}
	
		for(Integer dimID:dimsToDelete)
		{
			PocketManager.deletePocket(PocketManager.getDimensionData(dimID), true);
		}
		/**
		 * temporary workaround
		 */
		for(Integer dimID: dimsToFix)
		{
			PocketManager.getDimensionData(dimID).setParentToRoot();
		}
		//TODO- for some reason the parent field of loaded dimenions get reset to null if I call .setParentToRoot() before I delete the pockets. 
		//TODO implement blackList
		//Notify the user of the results
		sendChat(sender,("Reset complete. " + resetCount + " out of " + dungeonCount + " dungeons were reset."));
		return DDCommandResult.SUCCESS;
	}
}