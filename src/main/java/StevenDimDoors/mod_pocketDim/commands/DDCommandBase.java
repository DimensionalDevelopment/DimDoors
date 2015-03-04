package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

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
	
	@Override
	public final String getCommandName()
	{
		return name;
	}
	
	@Override
	public final String getCommandUsage(ICommandSender sender)
	{
		StringBuilder builder = new StringBuilder();
		builder.append('/');
		builder.append(name);
		builder.append(' ');
		builder.append(formats[0]);
		for (int index = 1; index < formats.length; index++)
		{
			builder.append(" OR /");
			builder.append(name);
			builder.append(' ');
			builder.append(formats[index]);
		}
		return builder.toString();
	}
	
	/*
	 * Method invoked by the server to execute a command. The call is forwarded to a derived class
	 * to provide the sending player directly.
	 */
	@Override
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
					sendChat(player, "Usage: " + name + " " + format);
				}
			}
			sendChat(player, result.getMessage());
		}
	}

	public static void sendChat(EntityPlayer player, String message)
	{
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
	}

    /*
     * The following two compareTo() methods are copied from CommandBase because it seems
     * that Dryware and Technic Jenkins don't have those functions defined. How in the world?
     * I have no idea. But it's breaking our builds. -_-  ~SenseiKiwi
     */
    @Override
	public int compareTo(ICommand command)
    {
        return this.getCommandName().compareTo(command.getCommandName());
    }

    @Override
	public int compareTo(Object other)
    {
        return this.compareTo((ICommand) other);
    }
}
