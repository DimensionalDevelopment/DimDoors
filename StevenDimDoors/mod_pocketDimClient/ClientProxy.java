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

		MinecraftForgeClient.preloadTexture(BLOCK_PNG);
		MinecraftForgeClient.preloadTexture(WARP_PNG);
		MinecraftForgeClient.preloadTexture(RIFT_PNG);

		MinecraftForgeClient.preloadTexture(RIFT2_PNG);
		

		
	}
	
	
	@Override
	public void loadTextures()
	{

		mod_pocketDim.itemStableFabric.setIconIndex(9).setItemName("451");  
		mod_pocketDim.itemRiftBlade.setIconIndex(10).setItemName("445");  

		mod_pocketDim.itemDimDoor.setIconIndex(8).setItemName("45");  
		mod_pocketDim.itemExitDoor.setIconIndex(7).setItemName("233");  
		mod_pocketDim.itemLinkSignature.setIconIndex(5).setItemName("5");  
		mod_pocketDim.itemRiftRemover.setIconIndex(6).setItemName("6");  
		mod_pocketDim.blockRift.blockIndexInTexture=0;
		mod_pocketDim.blockDimWall.blockIndexInTexture=0;
		mod_pocketDim.blockLimbo.blockIndexInTexture=15;
		mod_pocketDim.itemChaosDoor.setIconIndex(21).setItemName("9");  

		mod_pocketDim.blockDimWallPerm.blockIndexInTexture=0;
		mod_pocketDim.blockRift.blockIndexInTexture=200;
		mod_pocketDim.dimDoor.blockIndexInTexture=18;
	//	mod_pocketDim.dimRail.blockIndexInTexture=13;
		mod_pocketDim.ExitDoor.blockIndexInTexture=19;
		mod_pocketDim.chaosDoor.blockIndexInTexture=30;
		mod_pocketDim.transientDoor.blockIndexInTexture=200;

		mod_pocketDim.linkDimDoor.blockIndexInTexture=17;
		mod_pocketDim.linkExitDoor.blockIndexInTexture=20;

		
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());

		    
	    
	}
	@Override
	public  void printStringClient(String string)
	{
		
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage(string);
	}
	
}