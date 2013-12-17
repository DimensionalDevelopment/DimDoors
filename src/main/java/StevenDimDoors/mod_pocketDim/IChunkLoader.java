package StevenDimDoors.mod_pocketDim;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader 
{
	public void forceChunkLoading(Ticket ticket,int x, int z);
}
