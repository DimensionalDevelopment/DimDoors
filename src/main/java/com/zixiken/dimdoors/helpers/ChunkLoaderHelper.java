package com.zixiken.dimdoors.helpers;

import java.io.File;
import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.world.PocketBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import com.zixiken.dimdoors.experimental.BoundingBox;
import com.zixiken.dimdoors.IChunkLoader;
import com.zixiken.dimdoors.Point3D;
import com.zixiken.dimdoors.core.NewDimData;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ChunkLoaderHelper implements LoadingCallback {
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			boolean loaded = false;
			BlockPos pos = new BlockPos(ticket.getModData().getInteger("goldDimDoor"), ticket.getModData().getInteger("goldDimDoorY"), ticket.getModData().getInteger("goldDimDoorZ"));

			if (world.getBlockState(pos).getBlock() == DimDoors.goldenDimensionalDoor) {
				IChunkLoader loader = (IChunkLoader) world.getTileEntity(pos);
				if (!loader.isInitialized()) {
					loader.initialize(ticket);
					loaded = true;
				}
			}if (!loaded) {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

	public static Ticket createTicket(BlockPos pos, World world)
	{
		NBTTagCompound data;
		Ticket ticket = ForgeChunkManager.requestTicket(DimDoors.instance, world, Type.NORMAL);
		if (ticket != null)
		{
			data = ticket.getModData();
			data.setInteger("goldDimDoorX", pos.getX());
			data.setInteger("goldDimDoorY", pos.getY());
			data.setInteger("goldDimDoorZ", pos.getZ());
		}
		return ticket;
	}

	public static void forcePocketChunks(NewDimData pocket, Ticket ticket) {
		BoundingBox bounds = PocketBuilder.calculateDefaultBounds(pocket);
		BlockPos minCorner = bounds.minCorner();
		BlockPos maxCorner = bounds.maxCorner();
		int minX = minCorner.getX() >> 4;
		int minZ = minCorner.getZ() >> 4;
		int maxX = maxCorner.getX() >> 4;
		int maxZ = maxCorner.getZ() >> 4;
		int chunkX;
		int chunkZ;

		for (chunkX = minX; chunkX <= maxX; chunkX++) {
			for (chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
			}
		}
	}

	public static void loadForcedChunkWorlds(FMLServerStartingEvent event) {
		for (NewDimData data : PocketManager.getDimensions()) {
			if(data.isPocketDimension()) {
				String chunkDir = DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoors/pocketDimID" + data.id();

				File file = new File(chunkDir);

				if(file.exists()) {
					if(ForgeChunkManager.savedWorldHasForcedChunkTickets(file))
					{
						PocketManager.loadDimension(data.id());
					}
				}
			}
		}
	}
}