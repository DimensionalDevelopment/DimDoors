package StevenDimDoors.mod_pocketDimClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.CommonProxy;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;


public class ClientProxy extends CommonProxy
{
	
	@Override
	public void registerRenderers()
	{
		//MinecraftForgeClient.preloadTexture(BLOCK_PNG);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransTrapdoor.class, new RenderTransTrapdoor());
        //This code activates the new rift rendering, as well as a bit of code in TileEntityRift
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRift.class, new RenderRift());
        
		//MinecraftForgeClient.preloadTexture(RIFT2_PNG);
       RenderingRegistry.registerEntityRenderingHandler(MobMonolith.class, new RenderMobObelisk(.5F));		
       RenderingRegistry.registerBlockHandler(new PrivatePocketRender(RenderingRegistry.getNextAvailableRenderId()));
       
	}
	
	@Override
	public void updateDoorTE(BaseDimDoor door, World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityDimDoor)
		{
			DimLink link = PocketManager.getLink(x, y, z, world);
			int metadata = world.getBlockMetadata(x, y, z);
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;			
			dimTile.openOrClosed = door.isDoorOnRift(world, x, y, z)&&door.isUpperDoorBlock(metadata);
			dimTile.orientation = door.getFullMetadata(world, x, y, z) & 7;
			dimTile.lockStatus = door.getLockStatus(world, x, y, z);
		}
	}
	
	@Override
	public  void printStringClient(String string)
	{	
	}
	
}