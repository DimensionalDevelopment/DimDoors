package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class CommandTeleportPlayer extends DDCommandBase
{	
	private static CommandTeleportPlayer instance = null;
	
	private CommandTeleportPlayer()
	{
		super("dd-tp", new String[] {
				"<player name> <dimension number>",
				"<player name> <x> <y> <z>",
				"<player name> <dimension number> <x> <y> <z>"} );
	}
	
	public static CommandTeleportPlayer instance()
	{
		if (instance == null)
			instance = new CommandTeleportPlayer();

		return instance;
	}
	
	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command) 
	{
		int x;
		int y;
		int z;
		World world;
		int dimensionID;
		Point4D destination;
		NewDimData dimension;
		boolean checkOrientation;
		EntityPlayer targetPlayer;
		
		if (command.length < 2)
		{
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		}
		if (command.length > 5)
		{
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		if (command.length == 3)
		{
			return DDCommandResult.INVALID_ARGUMENTS;
		}
		// Check that all arguments after the username are integers
		for (int k = 1; k < command.length; k++)
		{
			if (!isInteger(command[k]))
			{
				return DDCommandResult.INVALID_ARGUMENTS;
			}
		}
		// Check if the target player is logged in
		targetPlayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(command[0]);
		if (targetPlayer == null)
		{
			return DDCommandResult.PLAYER_OFFLINE;
		}
		// If a dimension ID was provided, try to load it
		if (command.length != 4)
		{
			dimensionID = Integer.parseInt(command[1]);
			world = PocketManager.loadDimension(dimensionID);
			if (world == null)
			{
				return DDCommandResult.UNREGISTERED_DIMENSION;
			}
		}
		else
		{
			dimensionID = targetPlayer.worldObj.provider.dimensionId;
			world = targetPlayer.worldObj;
		}
		
		// If we teleport to a pocket dimension, set checkOrientation to true
		// so the player is placed correctly relative to the entrance door.
		checkOrientation = false;
		
		// Parse or calculate the destination as necessary
		// The Y coordinate must be increased by 1 because of the way that
		// DDTeleporter considers destination points. It assumes that the
		// point provided is the upper block of a door.
		if (command.length == 2)
		{
			// Check if the destination is a pocket dimension
			dimension = PocketManager.createDimensionData(world);
			if (dimension.isPocketDimension())
			{
				// The destination is a pocket dimension.
				// Teleport the player to its original entrance (the origin).
				destination = dimension.origin();
				checkOrientation = true;
			}
			else
			{
				// The destination is not a pocket dimension, which means we
				// don't automatically know a safe location where we can send
				// the player. Send the player to (0, Y, 0), where Y is chosen
				// by searching. Add 2 to place the player ABOVE the top block.
				y = world.getTopSolidOrLiquidBlock(0, 0) + 2;
				destination = new Point4D(0, y, 0, dimensionID);
			}
		}
		else if (command.length == 4)
		{
			x = Integer.parseInt(command[1]);
			y = Integer.parseInt(command[2]) + 1; // Correct the Y value
			z = Integer.parseInt(command[3]);
			destination = new Point4D(x, y, z, dimensionID);
		}
		else
		{
			x = Integer.parseInt(command[2]);
			y = Integer.parseInt(command[3]) + 1; // Correct the Y value
			z = Integer.parseInt(command[4]);
			destination = new Point4D(x, y, z, dimensionID);
		}
		// Teleport!
		DDTeleporter.teleportEntity(targetPlayer, destination, checkOrientation);
		return DDCommandResult.SUCCESS;
	}
	
	private static boolean isInteger(String input)
	{
		try  
		{  
			Integer.parseInt(input);  
			return true;  
		}  
		catch(Exception e)
		{  
			return false;  
		}  
	}
}