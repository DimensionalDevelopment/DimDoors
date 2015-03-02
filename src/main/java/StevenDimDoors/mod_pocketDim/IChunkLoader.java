package StevenDimDoors.mod_pocketDim;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader 
{
	public boolean isInitialized();
	public void initialize(Ticket ticket);
}
