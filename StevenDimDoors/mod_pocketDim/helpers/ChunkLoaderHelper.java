package StevenDimDoors.mod_pocketDim.helpers;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;

import com.google.common.collect.Lists;


public class ChunkLoaderHelper implements ForgeChunkManager.OrderedLoadingCallback 
{

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for (Ticket ticket : tickets) 
		{
			int goldDimDoorX = ticket.getModData().getInteger("goldDimDoorX");
			int goldDimDoorY = ticket.getModData().getInteger("goldDimDoorY");
			int goldDimDoorZ = ticket.getModData().getInteger("goldDimDoorZ");
			TileEntityDimDoorGold tile = (TileEntityDimDoorGold) world.getBlockTileEntity(goldDimDoorX, goldDimDoorY, goldDimDoorZ);
			tile.forceChunkLoading(ticket);
		
		}
	}

	@Override
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) 
	{
		List<Ticket> validTickets = Lists.newArrayList();
		for (Ticket ticket : tickets) 
		{
			int goldDimDoorX = ticket.getModData().getInteger("goldDimDoorX");
			int goldDimDoorY = ticket.getModData().getInteger("goldDimDoorY");
			int goldDimDoorZ = ticket.getModData().getInteger("goldDimDoorZ");
			
			int blId = world.getBlockId(goldDimDoorX, goldDimDoorY, goldDimDoorZ);
			if (blId == mod_pocketDim.properties.GoldDimDoorID) 
			{
				validTickets.add(ticket);
			}
		}
		return validTickets;
	}
}