package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;

public class CommandAddDungeonRift extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "add_dungeon_rift";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
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
		else if(var2.length!=0&&var2[0].equals("list"))
		{
			for(DungeonGenerator dungeonGen : mod_pocketDim.dungeonHelper.registeredDungeons)
			{
				String dungeonName =dungeonGen.schematicPath;
				if(dungeonName.contains("DimDoors_Custom_schematics"))
				{
					dungeonName=	dungeonName.substring(dungeonName.indexOf("DimDoors_Custom_schematics")+26);
				}
					
				dungeonName =dungeonName.replace("/", "").replace(".", "").replace("schematics", "").replace("schematic", "");
				
				
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer(dungeonName);

			}
			
			for(DungeonGenerator dungeonGen : mod_pocketDim.dungeonHelper.customDungeons)
			{
				String dungeonName =dungeonGen.schematicPath;
				if(dungeonName.contains("DimDoors_Custom_schematics"))
				{
					dungeonName=	dungeonName.substring(dungeonName.indexOf("DimDoors_Custom_schematics")+26);
				}
					
				dungeonName =dungeonName.replace("/", "").replace(".", "").replace("schematics", "").replace("schematic", "");
				
				
				this.getCommandSenderAsPlayer(var1).sendChatToPlayer(dungeonName);

			}
			
			
		}
		
		else if(var2.length!=0)
		{
			for(DungeonGenerator dungeonGen : mod_pocketDim.dungeonHelper.registeredDungeons)	
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
			
			for(DungeonGenerator dungeonGen : mod_pocketDim.dungeonHelper.customDungeons)	
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