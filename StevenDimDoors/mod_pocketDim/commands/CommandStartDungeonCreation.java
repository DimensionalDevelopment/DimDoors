package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.items.itemDimDoor;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommandStartDungeonCreation extends CommandBase
{
	public CommandStartDungeonCreation()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}
	
	private static DDProperties properties = null;
	
	public String getCommandName()//the name of our command
	{
		return "dimdoors-startDungeonCreation";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) 
	{

		EntityPlayer player = this.getCommandSenderAsPlayer(var1);
		
		int x = (int) player.posX;
		int y = (int) player.posY;
		int z = (int) player.posZ;
		
		if(!player.worldObj.isRemote)
		{

			LinkData link = new LinkData(player.worldObj.provider.dimensionId, 0, x, y+1, z, x, y+1, z, true, 3);
		
			link = dimHelper.instance.createPocket(link,true, false);
			
			itemDimDoor.placeDoorBlock(player.worldObj, x, y, z, 3, Block.blocksList[properties.WarpDoorID]);
		
		//	dimHelper.instance.teleportToPocket(player.worldObj, link, player);
		
			mod_pocketDim.dungeonHelper.customDungeonStatus.put(link.destDimID, dimHelper.instance.getLinkDataFromCoords(link.destXCoord, link.destYCoord, link.destZCoord, link.destDimID));
		
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("DimID = "+ link.destDimID);
		}
	
	}
}