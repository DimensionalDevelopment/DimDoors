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
	
	public DDCommandBase(String name)
	{
		this.name = name;
	}

	/*
	 * When overridden in a derived class, processes the command sent by the server.
	 */
	protected abstract void processCommand(EntityPlayer sender, String[] command);
	
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
		processCommand(getCommandSenderAsPlayer(sender), command);
	}
}
