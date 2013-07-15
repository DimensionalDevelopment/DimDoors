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
		super("dd-export", new String[] {
				"<dungeon type> <dungeon name> <'open' | 'closed'> [weight] ['override']",
				"<schematic name> override" } );
	}
	
	public static CommandEndDungeonCreation instance()
	{
		if (instance == null)
			instance = new CommandEndDungeonCreation();
		
		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		/*
		 * There are two versions of this command. One version takes 3 to 5 arguments consisting
		 * of the information needed for a proper schematic name and an optional override argument.
		 * The override argument only allows the user to export any dimension, even if it wasn't
		 * meant for custom dungeon creation. It does not allow the user to export a dungeon with
		 * invalid tags.
		 * 
		 * If the user wishes to name his schematic in a different format, then he will have to use
		 * the 2-argument version of this command, which accepts a schematic name and a mandatory
		 * override argument.
		 */
		
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		if (command.length < 2)
		{
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		}
		if (command.length > 5)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		
		//Check if we received the 2-argument version
		if (command.length == 2)
		{
			if (command[1].equalsIgnoreCase("override"))
			{
				//Check that the schematic name is a legal name
				if (DungeonHelper.SchematicNamePattern.matcher(command[0]).matches())
				{
					//Export the schematic
					return exportDungeon(sender, command[0]);
				}
				else
				{
					//The schematic name contains illegal characters. Inform the user.
					return new DDCommandResult("Error: Invalid schematic name. Please use only letters, numbers, dashes, and underscores.");
				}
			}
			else
			{
				//The command is malformed in some way. Assume that the user meant to use
				//the 3-argument version and report an error.
				return DDCommandResult.TOO_FEW_ARGUMENTS;
			}
		}
		
		//The user must have used the 3-argument version of this command
		//Check if the current dimension is a pocket for building custom dungeons or if the override argument was used.
		if (!dungeonHelper.isCustomDungeon(sender.worldObj.provider.dimensionId) &&
			!command[command.length - 1].equalsIgnoreCase("override"))
		{
			//This dimension may not be exported without overriding!
			return new DDCommandResult("Error: The current dimension was not made for dungeon creation. Use the 'override' argument to export anyway.");				
		}
		
		//TODO: Why do we check remoteness here but not before? And why not for the other export case?
		//Something feels wrong... ~SenseiKiwi
		
		if (!sender.worldObj.isRemote)
		{
			//TODO: This validation should be in DungeonHelper or in another class. We should move it
			//once the during the save file format rewrite. ~SenseiKiwi
			
			if (!dungeonHelper.validateDungeonType(command[0]))
			{
				return new DDCommandResult("Error: Invalid dungeon type. Please use one of the existing types.");
			}
			if (!DungeonHelper.DungeonNamePattern.matcher(command[1]).matches())
			{
				return new DDCommandResult("Error: Invalid dungeon name. Please use only letters, numbers, and dashes.");
			}
			if (!command[2].equalsIgnoreCase("open") && !command[2].equalsIgnoreCase("closed"))
			{
				return new DDCommandResult("Error: Please specify whether the dungeon is 'open' or 'closed'.");
			}
			
			//If there are no more argument, export the dungeon.
			if (command.length == 3)
			{
				return exportDungeon(sender, join(command, "_", 0, 3));
			}
			
			//Validate the 4th argument, which might be the weight or might be "override".
			try
			{
				int weight = Integer.parseInt(command[3]);
				if (weight >= 0 && weight <= DungeonHelper.MAX_DUNGEON_WEIGHT)
				{
					return exportDungeon(sender, join(command, "_", 0, 4));
				}
			}
			catch (Exception e)
			{
				//The 4th argument could be "override", but only if it's the last argument.
				//In that case, we assume the default dungeon weight.
				if (command.length == 4 && command[3].equalsIgnoreCase("override"))
				{
					return exportDungeon(sender, join(command, "_", 0, 3));
				}
			}
			
			//If we've reached this point, then we must have an invalid weight.
			return new DDCommandResult("Invalid dungeon weight. Please specify a weight between 0 and " + DungeonHelper.MAX_DUNGEON_WEIGHT + ", inclusive.");
		}
		
		return DDCommandResult.SUCCESS;
	}
	
	private static DDCommandResult exportDungeon(EntityPlayer player, String name)
	{
		DDProperties properties = DDProperties.instance();
		DungeonHelper dungeonHelper = DungeonHelper.instance();

		int x = (int) player.posX;
		int y = (int) player.posY;
		int z = (int) player.posZ;
		String exportPath = properties.CustomSchematicDirectory + File.separator + name + ".schematic";
		if (dungeonHelper.exportDungeon(player.worldObj, x, y, z, exportPath))
		{
			player.sendChatToPlayer("Saved dungeon schematic in " + exportPath);
			dungeonHelper.registerCustomDungeon(new File(exportPath));
			return DDCommandResult.SUCCESS;
		}
		else
		{
			return new DDCommandResult("Error: Failed to save dungeon schematic!");
		}
	}
	
	private static String join(String[] source, String delimiter, int start, int end)
	{
		//TODO: This function should be moved to a helper, but we have several single-function helpers as is.
		//I find that to be worse than keeping this private. ~SenseiKiwi
		
		int index;
		int length = 0;
		StringBuilder buffer;
		for (index = start; index < end; index++)
		{
			length += source[index].length();
		}
		length += (end - start - 1) * delimiter.length();
		
		buffer = new StringBuilder(length);
		buffer.append(source[start]);
		for (index = start + 1; index < end; index++)
		{
			buffer.append(delimiter);
			buffer.append(source[index]);
		}
		return buffer.toString();
	}
}