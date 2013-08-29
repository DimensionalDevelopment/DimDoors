package StevenDimDoors.mod_pocketDim.commands;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.BlankTeleporter;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;


public class CommandTeleportPlayer extends DDCommandBase
{	
	private static CommandTeleportPlayer instance = null;
	
	private CommandTeleportPlayer()
	{
		super("dd-tp", new String[] {"<Player Name> <Dimension ID> <X Coord> <Y Coord> <Z Coord>"} );
	}
	
	public static CommandTeleportPlayer instance()
	{
		if (instance == null)
		{
			instance = new CommandTeleportPlayer();
		}
		return instance;
	}

	/**
	 * TODO- Change to accept variety of input, like just coords, just dim ID, or two player names. 
	 */
	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command) 
	{
		List dimensionIDs = Arrays.asList(PocketManager.getStaticDimensionIDs()); //Gets list of all registered dimensions, regardless if loaded or not
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
			
			if(!dimensionIDs.contains(dimDestinationID))
			{
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
			if(PocketManager.getWorld(dimDestinationID)==null)
			{
				PocketManager.initDimension(dimDestinationID);
			}
			
			FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) targetPlayer, dimDestinationID, new BlankTeleporter(PocketManager.getWorld(dimDestinationID)));
			targetPlayer.setPositionAndUpdate(Integer.parseInt(command[2]),Integer.parseInt(command[3]),Integer.parseInt(command[4]));
		}
		else
		{
			return DDCommandResult.INVALID_ARGUMENTS;
		}
		return DDCommandResult.SUCCESS;
	}
	
    public boolean isInteger( String input )  
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