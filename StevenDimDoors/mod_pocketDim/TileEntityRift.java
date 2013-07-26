package StevenDimDoors.mod_pocketDim;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class TileEntityRift extends TileEntity

{
	public int xOffset=0;
	public int yOffset=0;
	public int zOffset=0;
	public int distance=0;
	public boolean hasGrownRifts=false;
	public boolean shouldClose=false;
	//public boolean isClosing=false;
	public boolean isNearRift=false;
	private int count=200;
	private int count2 = 0;
	private int soundCount = 0;
	public LinkData nearestRiftData;
	Random rand = new Random();
	
	
	
	 public boolean canUpdate()
	 {
		 return true;
	 }
	 
	 public void updateEntity() 
	 {
		
		if(count>200&&dimHelper.dimList.get(this.worldObj.provider.dimensionId)!=null)
		 {
			/**
			if(rand.nextBoolean())
			{
			}
			**/
			 nearestRiftData = dimHelper.dimList.get(this.worldObj.provider.dimensionId).findNearestRift(worldObj, 5, xCoord, yCoord, zCoord);
			 if(nearestRiftData!=null)
			 {
				 this.xOffset=this.xCoord-nearestRiftData.locXCoord;
				 this.yOffset=this.yCoord-nearestRiftData.locYCoord;
				 this.zOffset=this.zCoord-nearestRiftData.locZCoord;
				 this.distance=(int) (MathHelper.abs(xOffset)+MathHelper.abs(yOffset)+MathHelper.abs(zOffset));
				 this.isNearRift=true;
		 
				 if(!this.worldObj.isRemote&&distance>1)
				 {
					 try
					 {
						 grow(distance);
					 }
					 catch(Exception e)
					 {
						 
					 }
					 
				 }
				 if(rand.nextInt(30)==0&&!this.worldObj.isRemote)
				 {
					
					List list =  worldObj.getEntitiesWithinAABB(EntityEnderman.class, AxisAlignedBB.getBoundingBox( this.xCoord-9, this.yCoord-3, this.zCoord-9, this.xCoord+9, this.yCoord+3, this.zCoord+9));
					
					if(list.size()<1)
					{
					 
					 
					  EntityEnderman creeper = new EntityEnderman(worldObj);
	                  creeper.setLocationAndAngles(this.xCoord+.5, this.yCoord-1, this.zCoord+.5, 5, 6);
	                  worldObj.spawnEntityInWorld(creeper);
					}
					
					/**
					if(dimHelper.dimList.get(this.worldObj.provider.dimensionId)!=null)
					{
						ArrayList rifts = dimHelper.dimList.get(this.worldObj.provider.dimensionId).findRiftsInRange(worldObj, 6, this.xCoord, this.yCoord, this.zCoord);
						if(rifts.size()>15)
						{
							 MobObelisk creeper = new MobObelisk(worldObj);
			                  creeper.setLocationAndAngles(this.xCoord+.5, yCoordHelper.getFirstUncovered(this.worldObj, this.xCoord, this.yCoord, this.zCoord), this.zCoord+.5, 5, 6);
			                  worldObj.spawnEntityInWorld(creeper);
						}
						
					}
					**/
				 }
		 
		 
		 

			 }
			 else
			 {
				 this.isNearRift=false;
			 }
			 count=0;
		 }
		else if(dimHelper.instance.getLinkDataFromCoords(xCoord, yCoord, zCoord, this.worldObj.provider.dimensionId)==null)
		{
			this.invalidate();
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
		 count++;
		 
		 if(this.shouldClose)
		 {
			// System.out.println(count2);
			 if(count2>20&&count2<22)
			 {			 
				 nearestRiftData = dimHelper.dimList.get(this.worldObj.provider.dimensionId).findNearestRift(worldObj, 10, xCoord, yCoord, zCoord);
				 if(this.nearestRiftData!=null)
				 {
				 TileEntityRift rift = (TileEntityRift) this.worldObj.getBlockTileEntity(nearestRiftData.locXCoord, nearestRiftData.locYCoord, nearestRiftData.locZCoord);
				 if(rift!=null)
				 {
					 rift.shouldClose=true;
				 }
				 }
			 }
			 if(count2>40)
			 {
				 this.invalidate();
				 this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord,0);
				if(dimHelper.instance.getLinkDataFromCoords(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId)!=null)
				{
				 dimHelper.instance.removeLink(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord);
				 this.worldObj.playSound(xCoord, yCoord, zCoord, "mods.DimDoors.sfx.riftClose", (float) .7, 1,true);

				}
				
			 }
			 count2++;
		 }
		

	 }
	 public void grow(int distance)
	 {
		 int count=0;
		 if(rand.nextInt(distance*2)==0)
		 {
			
			 int x=0,y=0,z=0;
			 while(count<100)
			 {
				 count++;
				  x=this.xCoord+(1-(rand.nextInt(2)*2)*rand.nextInt(6));
				  y=this.yCoord+(1-(rand.nextInt(2)*2)*rand.nextInt(4));
				  z=this.zCoord+(1-(rand.nextInt(2)*2)*rand.nextInt(6));
				  if(this.worldObj.isAirBlock(x, y, z))
				  {
					  break;
				  }
				 
			 }
			
			 
			 if (count<100)
			 {
			
				LinkData link = dimHelper.instance.getLinkDataFromCoords(this.xCoord, this.yCoord, this.zCoord, worldObj);
				if(link!=null)
				{
					if(!this.hasGrownRifts&&rand.nextInt(3)==0)
					{
				//	System.out.println(link.numberofChildren);
					link.numberofChildren++;
					dimHelper.instance.createLink(this.worldObj.provider.dimensionId, link.destDimID, x, y, z, link.destXCoord, link.destYCoord, link.destZCoord).numberofChildren=link.numberofChildren+1;
					this.hasGrownRifts=true;

					}
				}
			 }
			
		 }
		 
	 }
	 
	 @Override
	    public void readFromNBT(NBTTagCompound nbt)
	    {
	        super.readFromNBT(nbt);
	        int i = nbt.getInteger(("Size"));

	        try
	        {
	            this.xOffset = nbt.getInteger("xOffset");
	            this.yOffset = nbt.getInteger("yOffset");
	            this.zOffset = nbt.getInteger("zOffset");
	            this.hasGrownRifts =nbt.getBoolean("grownRifts");
	            this.count=nbt.getInteger("count");
	            this.count2=nbt.getInteger("count2");
	            this.shouldClose=nbt.getBoolean("shouldClose");
  

	         
	           

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
	        nbt.setInteger("xOffset", this.xOffset);
	        nbt.setInteger("yOffset", this.yOffset);
	        nbt.setInteger("zOffset", this.zOffset);
	        nbt.setBoolean("grownRifts",this.hasGrownRifts);
	        nbt.setInteger("count", this.count);
	        nbt.setInteger("count2", this.count2);
	        nbt.setBoolean("shouldClose", this.shouldClose);
	       
	    }
}
