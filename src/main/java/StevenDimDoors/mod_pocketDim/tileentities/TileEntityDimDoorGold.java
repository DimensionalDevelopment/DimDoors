package StevenDimDoors.mod_pocketDim.tileentities;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;

public class TileEntityDimDoorGold extends TileEntityDimDoor implements IChunkLoader
{
	private Ticket chunkTicket;

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity() 
	{ // every tick?
		if (PocketManager.getDimensionData(this.worldObj) != null &&
				PocketManager.getDimensionData(this.worldObj).isPocketDimension() &&
				!this.worldObj.isRemote)
		{ 
			if(PocketManager.getLink(this.xCoord,this.yCoord,this.zCoord,this.worldObj)==null)
			{
				return;
			}
			if (this.chunkTicket == null)
			{
				chunkTicket = ForgeChunkManager.requestTicket(mod_pocketDim.instance, worldObj, Type.NORMAL);
				if(chunkTicket == null)
				{
					return;
				}
				chunkTicket.getModData().setInteger("goldDimDoorX", xCoord);
				chunkTicket.getModData().setInteger("goldDimDoorY", yCoord);
				chunkTicket.getModData().setInteger("goldDimDoorZ", zCoord);
				forceChunkLoading(chunkTicket,this.xCoord,this.zCoord);
			}
		}
	}

	@Override
	public void forceChunkLoading(Ticket chunkTicket,int x,int z)
	{
		Point4D origin = PocketManager.getDimensionData(this.worldObj).origin();
		int orientation = PocketManager.getDimensionData(this.worldObj).orientation();
		
		int xOffset=0;
		int zOffset=0;
		
		switch(orientation)
		{
		case 0:
			xOffset = PocketBuilder.DEFAULT_POCKET_SIZE/2;
			break;
		case 1: 
			zOffset = PocketBuilder.DEFAULT_POCKET_SIZE/2;

			break;
		case 2:
			xOffset = -PocketBuilder.DEFAULT_POCKET_SIZE/2;

			break;
		case 3:
			zOffset = -PocketBuilder.DEFAULT_POCKET_SIZE/2;

			break;
		}
		for(int chunkX = -2; chunkX<3;chunkX++)
		{
			for(int chunkZ = -2; chunkZ<3;chunkZ++)
			{
				ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair((origin.getX()+xOffset >> 4)+chunkX, (origin.getZ()+zOffset >> 4)+chunkZ));
			}
		}
	}

	@Override
	public void invalidate() 
	{
		ForgeChunkManager.releaseTicket(chunkTicket);
		super.invalidate();
	}
}
