package StevenDimDoors.mod_pocketDim.tileentities;

import StevenDimDoors.mod_pocketDim.IChunkLoader;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class TileEntityDimDoorGold extends TileEntityDimDoor implements IChunkLoader
{
	
	private Ticket chunkTicket;
	
	 public boolean canUpdate()
	 {
		 return true;
	 }
	 
	 public void updateEntity() 
	 {
		 if(PocketManager.getDimensionData(this.worldObj)!=null&&PocketManager.getDimensionData(this.worldObj).isPocketDimension()&&!this.worldObj.isRemote)
		 { 
			 if(this.chunkTicket==null)
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
	 
	 public void forceChunkLoading(Ticket chunkTicket,int x,int z)
	 {
		 if(PocketManager.getDimensionData(chunkTicket.world)==null)
		 {
			 return;
		 }
		 if(!PocketManager.getDimensionData(chunkTicket.world).isPocketDimension())
		 {
			 return;
		 }
		 
			for(int chunks = (PocketBuilder.DEFAULT_POCKET_SIZE/16)+1;chunks>0;chunks--)
			{
				ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair((xCoord >> 4)+chunks, (zCoord >> 4)+chunks));

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
	    {
	        super.readFromNBT(nbt);
	        int i = nbt.getInteger(("Size"));

	        try
	        {
	            this.openOrClosed = nbt.getBoolean("openOrClosed");
	            
	            this.orientation = nbt.getInteger("orientation");
	            
	            this.hasExit = nbt.getBoolean("hasExit");
	            
	            this.isDungeonChainLink = nbt.getBoolean("isDungeonChainLink");

	          

	           

	        }
	        catch (Exception e)
	        {
	            
	        }
	    }

	    @Override
	    public void writeToNBT(NBTTagCompound nbt)
	    {
	        int i = 0;
	        super.writeToNBT(nbt);
	        nbt.setBoolean("openOrClosed", this.openOrClosed);
	        
	        nbt.setBoolean("hasExit", this.hasExit);

	       	nbt.setInteger("orientation", this.orientation);
	       	
	       	nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);

          
	    }
}
