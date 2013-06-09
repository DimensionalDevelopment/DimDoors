package StevenDimDoors.mod_pocketDim.commands;

import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.customDungeonImporter;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandEndDungeonCreation extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "end_dungeon_creation";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		
		EntityPlayer player =this.getCommandSenderAsPlayer(var1);
		
		if(!customDungeonImporter.customDungeonStatus.containsKey(player.worldObj.provider.dimensionId))
		{
			if(var2.length==0)
			{
				player.sendChatToPlayer("Must have started dungeon creation, use argument OVERRIDE to export anyway");
				return;

			}
			else if(!var2[1].contains("OVERRIDE"))
			{
				player.sendChatToPlayer("Must have started dungeon creation, use argument OVERRIDE to export anyway");
				return;
	
			}

		}
		
		int x = (int) player.posX;
		int y = (int) player.posY;
		int z = (int) player.posZ;
		
		if(var2.length==0)
		{
			player.sendChatToPlayer("Must name file");
		}
		else
		{
			DungeonGenerator newDungeon = customDungeonImporter.exportDungeon(player.worldObj, x, y, z, mod_pocketDim.schematicContainer+"/"+var2[0]+".schematic");
			player.sendChatToPlayer("created dungeon schematic in " +mod_pocketDim.schematicContainer+"/"+var2[0]+".schematic");
			mod_pocketDim.customDungeons.add(newDungeon);
			
			dimHelper.instance.teleportToPocket(player.worldObj, customDungeonImporter.customDungeonStatus.get(player.worldObj.provider.dimensionId), player);

		}

		
	// TODO Auto-generated method stub
	
	}
}