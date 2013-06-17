package StevenDimDoors.mod_pocketDim.commands;

import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

public class CommandAddDungeonRift extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "dimdoors-genDungeonRift";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 
	{
		DungeonHelper dungeonHelper = DungeonHelper.instance();
		
		if(var2==null||this.getCommandSenderAsPlayer(var1).worldObj.isRemote)
		{
			return;
		}
		
		LinkData link = new LinkData(this.getCommandSenderAsPlayer(var1).worldObj.provider.dimensionId, 0,  
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posX),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posY)+1,
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posZ),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posX),
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posY)+1,
				MathHelper.floor_double(this.getCommandSenderAsPlayer(var1).posZ),true,3);
		
		
		
		
		if(var2.length!=0&&var2[0].equals("random"))
		{
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Created dungeon rift");
			dimHelper.instance.createLink(link);
			link = dimHelper.instance.createPocket(link,true, true);

		}
		else if (var2.length != 0 && var2[0].equals("list"))
		{
			Collection<String> dungeonNames = dungeonHelper.getDungeonNames();
			for (String name : dungeonNames)
			{
				getCommandSenderAsPlayer(var1).sendChatToPlayer(name);
			}
		}
		
		else if(var2.length!=0)
		{
			for(DungeonGenerator dungeonGen : dungeonHelper.registeredDungeons)	
			{
				String dungeonName =dungeonGen.schematicPath.toLowerCase();
				
				
				
				if(dungeonName.contains(var2[0].toLowerCase()))
				{

					link = dimHelper.instance.createPocket(link,true, true);
					
					dimHelper.dimList.get(link.destDimID).dungeonGenerator=dungeonGen;
				
					this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Genned dungeon " +dungeonName);


					return;

				}
				
				
				
			}
			
			for(DungeonGenerator dungeonGen : dungeonHelper.customDungeons)	
			{
				String dungeonName =dungeonGen.schematicPath.toLowerCase();
				
				
				
				if(dungeonName.contains(var2[0].toLowerCase()))
				{

					link = dimHelper.instance.createPocket(link,true, true);
					
					dimHelper.dimList.get(link.destDimID).dungeonGenerator=dungeonGen;
				
					this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Genned dungeon " +dungeonName);


					return;

				}
				
				
				
			}
			
			
			
			
			if(var2!=null&&!var2[0].equals("random"))
			{
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer("could not find dungeon, 'list' for list of dungeons");

			

			}
		}
		else
		{
			this.getCommandSenderAsPlayer(var1).sendChatToPlayer("invalid arguments- 'random' for random dungeon, or 'list' for dungeon names");

		}
		
		
		
		
		
		
	
				
			
			
		
		
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer(String.valueOf(var2.length));
	//	this.getCommandSenderAsPlayer(var1).sendChatToPlayer("Removed "+linksRemoved+" rifts.");

		
	// TODO Auto-generated method stub
	
	}
}