package StevenDimDoors.mod_pocketDim.commands;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.copyfile;
import StevenDimDoors.mod_pocketDim.world.pocketProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandEndDungeonCreation extends CommandBase
{
	public String getCommandName()//the name of our command
	{
		return "end_dungeon_creation";
	}




	@Override
	public void processCommand(ICommandSender var1, String[] var2) 

	{
		
		

		
	// TODO Auto-generated method stub
	
	}
}