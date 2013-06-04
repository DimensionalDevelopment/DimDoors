package StevenDimDoors.mod_pocketDim.commands;

import StevenDimDoors.mod_pocketDim.customDungeonImporter;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandEndDungeonCreation extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "end_dungeon_creation";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		int x = (int) this.getCommandSenderAsPlayer(var1).posX;
		int y = (int) this.getCommandSenderAsPlayer(var1).posY;
		int z = (int) this.getCommandSenderAsPlayer(var1).posZ;
		
		if(var2.length==0)
		{
			System.out.println("Must name file");
		}
		else
		{
			customDungeonImporter.exportDungeon(this.getCommandSenderAsPlayer(var1).worldObj, x, y, z, mod_pocketDim.schematicContainer+"/"+var2[0]);
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("created dungeon schematic in " +mod_pocketDim.schematicContainer+"/"+var2[0]);

		}

		
	// TODO Auto-generated method stub
	
	}
}