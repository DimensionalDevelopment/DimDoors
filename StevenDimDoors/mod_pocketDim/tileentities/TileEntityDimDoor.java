package StevenDimDoors.mod_pocketDim.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDimDoor extends TileEntity

{
	public boolean openOrClosed;
	public int orientation;
	public boolean hasExit;
	public boolean isDungeonChainLink;
	
	
	
	
	 public boolean canUpdate()
	 {
		 return false;
	 }
	 
	 public void updateEntity() 
	 {
     	

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
