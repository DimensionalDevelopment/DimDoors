package StevenDimDoors.mod_pocketDimClient;
import java.io.File;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import StevenDimDoors.mod_pocketDim.CommonProxy;
import StevenDimDoors.mod_pocketDim.Spells;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.ticking.CommonTickHandler;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;


public class ClientProxy extends CommonProxy
{
	
	@Override
	public void registerRenderers()
	{

		//MinecraftForgeClient.preloadTexture(BLOCK_PNG);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        //This code activates the new rift rendering, as well as a bit of code in TileEntityRift
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRift.class, new RenderRift());


		//MinecraftForgeClient.preloadTexture(RIFT2_PNG);
       RenderingRegistry.registerEntityRenderingHandler(MobMonolith.class, new RenderMobObelisk(.5F));
		
		

		
	}
	
	
  
	
	@Override
	public void loadTextures()
	{

		


	
		

		    
	    
	}
	@Override
	public  void printStringClient(String string)
	{
		
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage(string);
	}
	
}