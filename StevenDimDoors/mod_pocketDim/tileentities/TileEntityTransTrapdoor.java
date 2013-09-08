package StevenDimDoors.mod_pocketDim.tileentities;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityTransTrapdoor extends TileEntity

{
	public boolean hasRift;
	public boolean isShut;
	public int metaData;
	
	
	
	
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
    {
		if(newID==0&&PocketManager.getLink(x, y, z, world)!=null)
		{
			world.setBlock(x, y, z, mod_pocketDim.blockRift.blockID);
		}
        return true;
    }
	
	 public boolean canUpdate()
	 {
		 return true;
	 }
	 
	 public void updateEntity() 
	 {
     	System.out.println(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));

	 }
	 
	 @Override
	    public void readFromNBT(NBTTagCompound nbt)
	    {
	        super.readFromNBT(nbt);
	        int i = nbt.getInteger(("Size"));

	        try
	        {
	            this.hasRift = nbt.getBoolean("hasRift");
	            this.isShut = nbt.getBoolean("isShut");
	            
	            this.metaData = nbt.getInteger("metaData");
	            
	         
	          

	           

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
	        nbt.setBoolean("hasRift", this.hasRift);
	        nbt.setBoolean("isShut", this.isShut);
	        

	       	nbt.setInteger("metaData", this.metaData);
	       	

          
	    }
}
