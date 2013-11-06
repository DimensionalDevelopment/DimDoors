package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.event.FMLServerStartingEvent;


public class ChunkLoaderHelper implements LoadingCallback 
{

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for (Ticket ticket : tickets) 
		{
			int goldDimDoorX = ticket.getModData().getInteger("goldDimDoorX");
			int goldDimDoorY = ticket.getModData().getInteger("goldDimDoorY");
			int goldDimDoorZ = ticket.getModData().getInteger("goldDimDoorZ");
			IChunkLoader tile = (IChunkLoader) world.getBlockTileEntity(goldDimDoorX, goldDimDoorY, goldDimDoorZ);
			tile.forceChunkLoading(ticket,goldDimDoorX,goldDimDoorZ);
		
		}
	}

	public static void loadChunkForcedWorlds(FMLServerStartingEvent event)
	{
		for(NewDimData data : PocketManager.getDimensions())
		{
			if(data.isPocketDimension())
			{
				String chunkDir = DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoors/pocketDimID" + data.id();
				
				File file = new File(chunkDir);
				
				if(file.exists())
				{
					if(ForgeChunkManager.savedWorldHasForcedChunkTickets(file))
					{
						PocketManager.loadDimension(data.id());
					}
				}

			}
		}
		
	}
}