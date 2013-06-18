package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

public class CommandStartDungeonCreation extends DDCommandBase
{
	private static CommandStartDungeonCreation instance = null;
	
	private CommandStartDungeonCreation()
	{
		super("dd-create");
	}
	
	public static CommandStartDungeonCreation instance()
	{
		if (instance == null)
			instance = new CommandStartDungeonCreation();
		
		return instance;
	}

	@Override
	protected void processCommand(EntityPlayer sender, String[] command)
	{
		if (!sender.worldObj.isRemote)
		{
			//Place a door leading to a pocket dimension where the player is standing.
			//The pocket dimension will be serve as a room for the player to build a dungeon.
			int x = (int) sender.posX;
			int y = (int) sender.posY;
			int z = (int) sender.posZ;
			LinkData link = DungeonHelper.instance().createCustomDungeonDoor(sender.worldObj, x, y, z);
			
			//Notify the player
			sender.sendChatToPlayer("Created a door to a pocket dimension (ID = " + link.destDimID + "). Please build your dungeon there.");
		}
	}
}