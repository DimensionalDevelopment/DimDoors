package StevenDimDoors.mod_pocketDim.commands;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandCreateDungeonRift extends DDCommandBase
{
	private static CommandCreateDungeonRift instance = null;
	
	private CommandCreateDungeonRift()
	{
		super("dd-rift");
	}
	
	public static CommandCreateDungeonRift instance()
	{
		if (instance == null)
			instance = new CommandCreateDungeonRift();
		
		return instance;
	}

	@Override
	public void processCommand(EntityPlayer sender, String[] command)
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		if(command==null||sender.worldObj.isRemote)
		{
			return;
		}
		
		LinkData link = new LinkData(sender.worldObj.provider.dimensionId, 0,  
				(int) sender.posX,
				(int) sender.posY + 1,
				(int) sender.posZ,
				(int) sender.posX,
				(int) sender.posY + 1,
				(int) sender.posZ,true,3);
		
		if(command.length!=0&&command[0].equals("random"))
		{
			sender.sendChatToPlayer("Created dungeon rift");
			dimHelper.instance.createLink(link);
			link = dimHelper.instance.createPocket(link,true, true);
		}
		else if (command.length != 0 && command[0].equals("list"))
		{
			Collection<String> dungeonNames = dungeonHelper.getDungeonNames();
			for (String name : dungeonNames)
			{
				getCommandSenderAsPlayer(sender).sendChatToPlayer(name);
			}
		}
		else if(command.length!=0)
		{
			for(DungeonGenerator dungeonGen : dungeonHelper.registeredDungeons)	
			{
				String dungeonName =dungeonGen.schematicPath.toLowerCase();
				
				if(dungeonName.contains(command[0].toLowerCase()))
				{
					link = dimHelper.instance.createPocket(link,true, true);
					dimHelper.dimList.get(link.destDimID).dungeonGenerator=dungeonGen;
					sender.sendChatToPlayer("Genned dungeon " +dungeonName);
					return;
				}	
			}
			for(DungeonGenerator dungeonGen : dungeonHelper.customDungeons)	
			{
				String dungeonName =dungeonGen.schematicPath.toLowerCase();
				
				if(dungeonName.contains(command[0].toLowerCase()))
				{
					link = dimHelper.instance.createPocket(link,true, true);
					dimHelper.dimList.get(link.destDimID).dungeonGenerator=dungeonGen;
					sender.sendChatToPlayer("Genned dungeon " +dungeonName);
					return;
				}
			}
			if(command!=null&&!command[0].equals("random"))
			{
				sender.sendChatToPlayer("could not find dungeon, 'list' for list of dungeons");
			}
		}
		else
		{
			sender.sendChatToPlayer("invalid arguments- 'random' for random dungeon, or 'list' for dungeon names");
		}
	}
}