package StevenDimDoors.mod_pocketDim.commands;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

public class CommandEndDungeonCreation extends CommandBase
{
	private static DDProperties properties = null;
	
	public CommandEndDungeonCreation()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}
	
	public String getCommandName()//the name of our command
	{
		return "dimdoors-endDungeonCreation";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) 
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		EntityPlayer player = this.getCommandSenderAsPlayer(var1);
		
		if (!dungeonHelper.customDungeonStatus.containsKey(player.worldObj.provider.dimensionId))
		{
			if (var2.length < 2)
			{
				player.sendChatToPlayer("Must have started dungeon creation, use argument OVERRIDE to export anyway");
				return;

			}
		
			else if (!var2[1].contains("OVERRIDE"))
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
		else if(!player.worldObj.isRemote)
		{
			//Check that the dungeon name is valid to prevent directory traversal and other forms of abuse
			if (DungeonHelper.NamePattern.matcher(var2[0]).matches())
			{
				String exportPath = properties.CustomSchematicDirectory + "/" + var2[0] + ".schematic";
				if (dungeonHelper.exportDungeon(player.worldObj, x, y, z, exportPath))
				{
					player.sendChatToPlayer("Saved dungeon schematic in " + exportPath);
					dungeonHelper.registerCustomDungeon(new File(exportPath));
					
					if (dungeonHelper.customDungeonStatus.containsKey(player.worldObj.provider.dimensionId) && !player.worldObj.isRemote)
					{
					//	mod_pocketDim.dungeonHelper.customDungeonStatus.remove(player.worldObj.provider.dimensionId);
					//	dimHelper.instance.teleportToPocket(player.worldObj, mod_pocketDim.dungeonHelper.customDungeonStatus.get(player.worldObj.provider.dimensionId), player);
		
					}
				}
				else
				{
					player.sendChatToPlayer("Failed to save dungeon schematic!");
				}
			}
			else
			{
				player.sendChatToPlayer("Invalid schematic name. Please use only letters, numbers, and underscores.");
			}
		}

		
	// TODO Auto-generated method stub
	
	}
}