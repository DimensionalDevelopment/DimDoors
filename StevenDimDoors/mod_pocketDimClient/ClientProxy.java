package StevenDimDoors.mod_pocketDimClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import StevenDimDoors.mod_pocketDim.CommonProxy;
import StevenDimDoors.mod_pocketDim.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;


public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{

		//MinecraftForgeClient.preloadTexture(BLOCK_PNG);
		MinecraftForgeClient.preloadTexture(WARP_PNG);
		MinecraftForgeClient.preloadTexture(RIFT_PNG);

		//MinecraftForgeClient.preloadTexture(RIFT2_PNG);
		

		
	}
	
	
	@Override
	public void loadTextures()
	{

		



		
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());

		    
	    
	}
	@Override
	public  void printStringClient(String string)
	{
		
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage(string);
	}
	
}