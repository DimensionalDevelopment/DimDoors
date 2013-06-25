package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/*
 * An abstract base class for our Dimensional Doors commands. This cleans up the code a little and provides
 * some convenience improvements.
 */
public abstract class DDCommandBase extends CommandBase
{
	private String name;
	private String[] formats;
	
	public DDCommandBase(String name, String format)
	{
		this.name = name;
		this.formats = new String[] { format };
	}

	public DDCommandBase(String name, String[] formats)
	{
		this.name = name;
		this.formats = formats;
	}
	
	/*
	 * When overridden in a derived class, processes the command sent by the server
	 * and returns a status code and message for the result of the operation.
	 */
	protected abstract DDCommandResult processCommand(EntityPlayer sender, String[] command);
	
	public final String getCommandName()
	{
		return name;
	}
	
	/*
	 * Registers the command at server startup.
	 */
	public void register(FMLServerStartingEvent event)
	{
		event.registerServerCommand(this);
	}
	
	/*
	 * Method invoked by the server to execute a command. The call is forwarded to a derived class
	 * to provide the sending player directly.
	 */
	public final void processCommand(ICommandSender sender, String[] command)
	{
		//Forward the command
		EntityPlayer player = getCommandSenderAsPlayer(sender);
		DDCommandResult result = processCommand(player, command);

		//If the command failed, send the player a status message.
		if (result.failed())
		{
			if (result.shouldPrintUsage())
			{
				//Send the argument formats for this command
				for (String format : formats)
				{
					player.sendChatToPlayer("Usage: " + name + " " + format);
				}
			}
			player.sendChatToPlayer(result.getMessage());
		}
	}
}
