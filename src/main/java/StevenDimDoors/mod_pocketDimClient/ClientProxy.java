package StevenDimDoors.mod_pocketDimClient;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.CommonProxy;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;


public class ClientProxy extends CommonProxy
{
	
	@Override
	public void registerRenderers()
	{
		//MinecraftForgeClient.preloadTexture(BLOCK_PNG);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransTrapdoor.class, new RenderTransTrapdoor());
        
		//MinecraftForgeClient.preloadTexture(RIFT2_PNG);
       RenderingRegistry.registerEntityRenderingHandler(MobMonolith.class, new RenderMobObelisk(.5F));		
       RenderingRegistry.registerBlockHandler(new PrivatePocketRender(RenderingRegistry.getNextAvailableRenderId()));
       
	}
	
	@Override
	public void updateDoorTE(BaseDimDoor door, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityDimDoor)
		{
			DimLink link = PocketManager.getLink(x, y, z, world);
			int metadata = world.getBlockMetadata(x, y, z);
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;			
			dimTile.openOrClosed = door.isDoorOnRift(world, x, y, z)&&door.isUpperDoorBlock(metadata);
			dimTile.orientation = door.func_150012_g(world, x, y, z) & 7;
			dimTile.lockStatus = door.getLockStatus(world, x, y, z);
		}
	}
	
	@Override
	public  void printStringClient(String string)
	{	
	}

    @Override
    public void registerSidedHooks(DDProperties properties) {
        ClientOnlyHooks hooks = new ClientOnlyHooks(properties);
        MinecraftForge.EVENT_BUS.register(hooks);
        MinecraftForge.TERRAIN_GEN_BUS.register(hooks);
        PocketManager.getDimwatcher().registerReceiver (new PocketManager.ClientDimWatcher());
        PocketManager.getLinkWatcher().registerReceiver(new PocketManager.ClientLinkWatcher());
    }
}