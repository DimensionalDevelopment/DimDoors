package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ExitDoor extends dimDoor
{

	protected ExitDoor(int par1, Material par2Material) 
	{
		
		super(par1, Material.wood);
	//	this.blockIndexInTexture = 19;
        this.setTextureFile("/PocketBlockTextures.png");

		// TODO Auto-generated constructor stub
	}
	
	 @Override
	 public String getTextureFile()
	 {
		 return "/PocketBlockTextures.png";
	 }
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		
		
		if(!par1World.isRemote&&par1World.getBlockId(par2, par3-1, par4)==this.blockID)
		{
			
		
			int locDimID=par1World.provider.dimensionId;
			
			if(dimHelper.instance.dimList.containsKey(locDimID)&&dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)==null)
			{
				DimData dimData = dimHelper.dimList.get(locDimID);
				int ExitDimID = dimData.exitDimLink.destDimID;
	   		
	   		
	   			if(dimHelper.instance.getDimDepth(locDimID)==1)			
	   			{
	   				//System.out.println("exitToOverowrld from "+String.valueOf(locDimID));
	   				
				   	
				   		
						
					dimHelper.instance.createLink(locDimID, ExitDimID, par2, par3, par4, par2, par3, par4,par1World.getBlockMetadata(par2, par3-1, par4));
					dimHelper.instance.createLink(ExitDimID, locDimID, par2, par3, par4, par2, par3, par4,dimHelper.instance.flipDoorMetadata(par1World.getBlockMetadata(par2, par3-1, par4)));

					


	   			}
	   			else if(dimHelper.dimList.get(par1World.provider.dimensionId).isPocket)
	   			{
	   				//System.out.println("Created new dim from "+String.valueOf(par1World.provider.dimensionId));

	   				LinkData link = new LinkData(par1World.provider.dimensionId, 0, par2, par3, par4, par2, par3, par4, true);
					link.linkOrientation= par1World.getBlockMetadata(par2, par3-1, par4);
					dimHelper.instance.createPocket(link,false, false);

				
	   				
	   			//	dimHelper.instance.generatePocket(dimHelper.getWorld(destDimID), par2, par3, par4,par1World.getBlockMetadata(par2, par3-1, par4));


	   			}
   		 	
			}
			else if (dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
			{
				
				//System.out.println("RiftPresent at "+String.valueOf(par1World.provider.dimensionId));
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation=par1World.getBlockMetadata(par2, par3-1, par4);
				

				

				

			}
			
		
		
		}
		
		//this.onPoweredBlockChange(par1World, par2, par3, par4, false);
		
	}
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
        int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
       
    	int num = par1World.getBlockMetadata(par2, par3-1, par4);
    	if(!par1World.isRemote&&(num==5||num==4||num==6||num==7)&&(num-4)==var12&&par1World.getBlockId(par2, par3-1, par4)==mod_pocketDim.ExitDoorID||!par1World.isRemote&&(num==5||num==4||num==6||num==7)&&par1World.getBlockId(par2, par3-1, par4)==mod_pocketDim.ExitDoorID&&!(par5Entity instanceof EntityPlayer) )
			{
    
    		 //int destinationID= dimHelper.instance.getDestIDFromCoords(par2, par3, par4, par1World);
    		 
    		 this.onPoweredBlockChange(par1World, par2, par3, par4, false);

    		 LinkData linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
    		 if(linkData!=null)
    		 {
    			 if(dimHelper.dimList.containsKey(linkData.destDimID))
    			 dimHelper.instance.teleportToPocket(par1World, linkData, par5Entity);
    		 }
     			
    		
    	
		}
    }
	
	 public int idPicked(World par1World, int par2, int par3, int par4)
	    {
	        return Item.doorWood.itemID;
	    }
	    
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return (par1 & 8) != 0 ? 0 : (Item.doorWood.itemID);
	    }
	
	
	

}