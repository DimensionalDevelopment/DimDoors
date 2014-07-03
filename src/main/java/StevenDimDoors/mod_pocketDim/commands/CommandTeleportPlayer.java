package StevenDimDoors.mod_pocketDim.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class CommandTeleportPlayer extends DDCommandBase
{	
	private static CommandTeleportPlayer instance = null;
	
	private CommandTeleportPlayer()
	{
		super("dd-tp", new String[] {"<Player Name> <Dimension ID> <X Coord> <Y Coord> <Z Coord>","<Player Name> <Dimension ID>"} );
	}
	
	public static CommandTeleportPlayer instance()
	{
		if (instance == null)
			instance = new CommandTeleportPlayer();

		return instance;
	}
	
	/**
	 * TODO- Change to accept variety of input, like just coords, just dim ID, or two player names. 
	 */
	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command) 
	{
		EntityPlayer targetPlayer = sender;
		int dimDestinationID = sender.worldObj.provider.dimensionId;
		
		if(command.length == 5)
		{
			for(int i= 1; i <5;i++)
			{
				if(!isInteger(command[i]))
				{
					return DDCommandResult.INVALID_ARGUMENTS;
				}
			}
			if(sender.worldObj.getPlayerEntityByName(command[0])!=null) //Gets the targeted player
			{
				targetPlayer = sender.worldObj.getPlayerEntityByName(command[0]);
			}
			else
			{
				return DDCommandResult.INVALID_ARGUMENTS;
			}
			dimDestinationID=Integer.parseInt(command[1]);//gets the target dim ID from the command string
			
			if(!DimensionManager.isDimensionRegistered(dimDestinationID))
			{
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
	
			PocketManager.loadDimension(dimDestinationID);
			Point4D destination = new Point4D(Integer.parseInt(command[2]),Integer.parseInt(command[3]),Integer.parseInt(command[4]),dimDestinationID);
			DDTeleporter.teleportEntity(targetPlayer, destination, false);
		}
		else if(command.length == 2 && isInteger(command[1]))
		{
			if(sender.worldObj.getPlayerEntityByName(command[0])!=null) //Gets the targeted player
			{
				targetPlayer = sender.worldObj.getPlayerEntityByName(command[0]);
			}
			else
			{
				return DDCommandResult.INVALID_ARGUMENTS;
			}
			dimDestinationID=Integer.parseInt(command[1]);//gets the target dim ID from the command string
			
			if(!DimensionManager.isDimensionRegistered(dimDestinationID))
			{
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
	
		
			Point4D destination = PocketManager.getDimensionData(dimDestinationID).origin();
			if(!PocketManager.getDimensionData(dimDestinationID).isPocketDimension())
			{
				destination = new Point4D(destination.getX(),PocketManager.loadDimension(dimDestinationID).getTopSolidOrLiquidBlock(
						destination.getX(), destination.getZ()),
						destination.getZ(),destination.getDimension());
			}
			DDTeleporter.teleportEntity(targetPlayer, destination, false);
		}
		else if(command.length == 1 && isInteger(command[0]))
		{
			
			targetPlayer = sender;
			
			dimDestinationID=Integer.parseInt(command[0]);//gets the target dim ID from the command string
			
			if(!DimensionManager.isDimensionRegistered(dimDestinationID))
			{
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
	
			
			Point4D destination = PocketManager.getDimensionData(dimDestinationID).origin();
			if(!PocketManager.getDimensionData(dimDestinationID).isPocketDimension())
			{
				destination = new Point4D(destination.getX(),PocketManager.loadDimension(dimDestinationID).getTopSolidOrLiquidBlock(
						destination.getX(), destination.getZ()),
						destination.getZ(),destination.getDimension());
			}
			DDTeleporter.teleportEntity(targetPlayer, destination, false);
		}
		else	
		{
			return DDCommandResult.INVALID_ARGUMENTS;
		}
		return DDCommandResult.SUCCESS;
	}
	
    public static boolean isInteger( String input )  
    {  
       try  
       {  
          Integer.parseInt( input );  
          return true;  
       }  
       catch(Exception e )  
       {  
          return false;  
       }  
    }  
	
}