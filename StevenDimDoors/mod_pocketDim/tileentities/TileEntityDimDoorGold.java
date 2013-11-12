package StevenDimDoors.mod_pocketDim.tileentities;

import java.awt.List;

import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import net.minecraft.block.Block;
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
			if(PocketManager.getLink(this.xCoord,this.yCoord,this.zCoord,this.worldObj)==null)
			{
				return;
			}
			if (this.chunkTicket == null)
			{
				chunkTicket = ForgeChunkManager.requestTicket(mod_pocketDim.instance, worldObj, Type.NORMAL);
				chunkTicket.getModData().setInteger("goldDimDoorX", xCoord);
				chunkTicket.getModData().setInteger("goldDimDoorY", yCoord);
				chunkTicket.getModData().setInteger("goldDimDoorZ", zCoord);
				forceChunkLoading(chunkTicket,this.xCoord,this.zCoord);
			}
			
			for(Object chunk : this.chunkTicket.getChunkList())
			{
				for(int x = 0; x<16;x++)
				{
					for(int z = 0; z<16;z++)
					{
						this.worldObj.setBlock(((ChunkCoordIntPair)chunk).chunkXPos*16 + x, this.yCoord-2, ((ChunkCoordIntPair)chunk).chunkZPos*16 + z, Block.glowStone.blockID);
					}
				}
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
		for(int chunkX = -1; chunkX<2;chunkX++)
		{
			for(int chunkZ = -1; chunkZ<2;chunkZ++)
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
