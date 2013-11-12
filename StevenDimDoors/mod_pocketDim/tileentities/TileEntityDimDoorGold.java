package StevenDimDoors.mod_pocketDim.tileentities;

import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

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
			if (this.chunkTicket == null)
			{
				chunkTicket = ForgeChunkManager.requestTicket(mod_pocketDim.instance, worldObj, Type.NORMAL);
			}

			chunkTicket.getModData().setInteger("goldDimDoorX", xCoord);
			chunkTicket.getModData().setInteger("goldDimDoorY", yCoord);
			chunkTicket.getModData().setInteger("goldDimDoorZ", zCoord);
			ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
			forceChunkLoading(chunkTicket,this.xCoord,this.zCoord);
		}
	}

	@Override
	public void forceChunkLoading(Ticket chunkTicket,int x,int z)
	{
		NewDimData data = PocketManager.getDimensionData(chunkTicket.world);
		if (data == null)
		{
			return;
		}
		if (!data.isPocketDimension())
		{
			return;
		}
		
		Point4D origin = data.origin();
		switch (data.orientation())
		{
		
		}
		//TODO fix this
		for(int chunksX = (PocketBuilder.DEFAULT_POCKET_SIZE/16) + 1; chunksX > 0; --chunksX)
		{
			ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair((xCoord >> 4) + chunksX,
					(zCoord >> 4)));
			
			for(int chunksZ = (PocketBuilder.DEFAULT_POCKET_SIZE/16) + 1; chunksZ > 0; --chunksZ)
			{
				ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair((xCoord >> 4),
						(zCoord >> 4) + chunksZ));
			}	
		}	
	}

	@Override
	public void invalidate() 
	{
		ForgeChunkManager.releaseTicket(chunkTicket);
		super.invalidate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{ // this and write both call user, and super saves/reads all the same data. why override at all?
		super.readFromNBT(nbt);
		@SuppressWarnings("unused") // ???
		int i = nbt.getInteger(("Size"));

		try
		{
			this.openOrClosed = nbt.getBoolean("openOrClosed");
			this.orientation = nbt.getInteger("orientation");
			this.hasExit = nbt.getBoolean("hasExit");
			this.isDungeonChainLink = nbt.getBoolean("isDungeonChainLink");
		}
		catch (Exception e) // ???
		{

		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		@SuppressWarnings("unused") // ?????
		int i = 0;
		super.writeToNBT(nbt);
		
		nbt.setBoolean("openOrClosed", this.openOrClosed);
		nbt.setBoolean("hasExit", this.hasExit);
		nbt.setInteger("orientation", this.orientation);
		nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);
	}
}
