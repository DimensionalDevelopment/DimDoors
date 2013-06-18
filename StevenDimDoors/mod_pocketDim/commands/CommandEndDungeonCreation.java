package StevenDimDoors.mod_pocketDim.commands;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

public class CommandEndDungeonCreation extends DDCommandBase
{	
	private static CommandEndDungeonCreation instance = null;
	
	private CommandEndDungeonCreation()
	{
		super("dd-export");
	}
	
	public static CommandEndDungeonCreation instance()
	{
		if (instance == null)
			instance = new CommandEndDungeonCreation();
		
		return instance;
	}

	@Override
	protected void processCommand(EntityPlayer sender, String[] command)
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		DDProperties properties = DDProperties.instance();
		
		if (!dungeonHelper.isCustomDungeon(sender.worldObj.provider.dimensionId))
		{
			if (command.length < 2)
			{
				sender.sendChatToPlayer("Must have started dungeon creation, use argument OVERRIDE to export anyway");
				return;

			}
		
			else if (!command[1].contains("OVERRIDE"))
			{
				sender.sendChatToPlayer("Must have started dungeon creation, use argument OVERRIDE to export anyway");
				return;
	
			}

		}
		
		int x = (int) sender.posX;
		int y = (int) sender.posY;
		int z = (int) sender.posZ;
		
		if (command.length == 0)
		{
			sender.sendChatToPlayer("Must name file");
		}
		else if(!sender.worldObj.isRemote)
		{
			//Check that the dungeon name is valid to prevent directory traversal and other forms of abuse
			if (DungeonHelper.NamePattern.matcher(command[0]).matches())
			{
				String exportPath = properties.CustomSchematicDirectory + "/" + command[0] + ".schematic";
				if (dungeonHelper.exportDungeon(sender.worldObj, x, y, z, exportPath))
				{
					sender.sendChatToPlayer("Saved dungeon schematic in " + exportPath);
					dungeonHelper.registerCustomDungeon(new File(exportPath));
				}
				else
				{
					sender.sendChatToPlayer("Failed to save dungeon schematic!");
				}
			}
			else
			{
				sender.sendChatToPlayer("Invalid schematic name. Please use only letters, numbers, and underscores.");
			}
		}
	}
}