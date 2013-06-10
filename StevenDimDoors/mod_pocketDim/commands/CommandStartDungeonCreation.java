package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.customDungeonImporter;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommandStartDungeonCreation extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "start_dungeon_creation";
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
		
			dimHelper.instance.teleportToPocket(player.worldObj, link, player);
		
			customDungeonImporter.customDungeonStatus.put(player.worldObj.provider.dimensionId, dimHelper.instance.getLinkDataFromCoords(link.destXCoord, link.destYCoord, link.destZCoord, link.destDimID));
		
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("DimID = "+ link.destDimID);
		}
	
	}
}