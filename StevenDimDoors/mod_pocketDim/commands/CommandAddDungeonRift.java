package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandAddDungeonRift extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "add_dungeon_rift";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		
		
		LinkData link = new LinkData(this.getCommandSenderAsPlayer(var1).worldObj.provider.dimensionId, 0,  
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posX),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posY)+1,
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posZ),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posX),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posY)+1,
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posZ),true);
		
		link = dimHelper.instance.createPocket(link,true, true);
		
		
		
		
	
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Created dungeon rift");
				
			
			
		
		
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2.length));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+linksRemoved+" rifts.");

		
	// TODO Auto-generated method stub
	
	}
}