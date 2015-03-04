package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import StevenDimDoors.experimental.BoundingBox;
import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ChunkLoaderHelper implements LoadingCallback 
{
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for (Ticket ticket : tickets) 
		{
			boolean loaded = false;
			int x = ticket.getModData().getInteger("goldDimDoorX");
			int y = ticket.getModData().getInteger("goldDimDoorY");
			int z = ticket.getModData().getInteger("goldDimDoorZ");

			if (world.getBlock(x, y, z) == mod_pocketDim.goldenDimensionalDoor)
			{
				IChunkLoader loader = (IChunkLoader) world.getTileEntity(x, y, z);
				if (!loader.isInitialized())
				{
					loader.initialize(ticket);
					loaded = true;
				}
			}
			if (!loaded)
			{
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

	public static Ticket createTicket(int x, int y, int z, World world)
	{
		NBTTagCompound data;
		Ticket ticket = ForgeChunkManager.requestTicket(mod_pocketDim.instance, world, Type.NORMAL);
		if (ticket != null)
		{
			data = ticket.getModData();
			data.setInteger("goldDimDoorX", x);
			data.setInteger("goldDimDoorY", y);
			data.setInteger("goldDimDoorZ", z);
		}
		return ticket;
	}

	public static void forcePocketChunks(NewDimData pocket, Ticket ticket)
	{
		BoundingBox bounds = PocketBuilder.calculateDefaultBounds(pocket);
		Point3D minCorner = bounds.minCorner();
		Point3D maxCorner = bounds.maxCorner();
		int minX = minCorner.getX() >> 4;
		int minZ = minCorner.getZ() >> 4;
		int maxX = maxCorner.getX() >> 4;
		int maxZ = maxCorner.getZ() >> 4;
		int chunkX;
		int chunkZ;

		for (chunkX = minX; chunkX <= maxX; chunkX++)
		{
			for (chunkZ = minZ; chunkZ <= maxZ; chunkZ++)
			{
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
			}
		}
	}

	public static void loadForcedChunkWorlds(FMLServerStartingEvent event)
	{
		for (NewDimData data : PocketManager.getDimensions())
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