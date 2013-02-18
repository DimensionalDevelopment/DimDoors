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

public class linkDimDoor extends dimDoor
{

	protected linkDimDoor(int par1, Material par2Material) 
	{
		
		super(par1, par2Material);
		//this.blockIndexInTexture = 17;
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
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
			{
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation=par1World.getBlockMetadata(par2, par3-1, par4);

			}
			else
			{
				System.out.println("couldnt find parent link!!!!!!");
			}
		
		}
		//this.onPoweredBlockChange(par1World, par2, par3, par4, false);
		
	}
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
    	
        int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
       
    	int num = par1World.getBlockMetadata(par2, par3-1, par4);
    	if(!par1World.isRemote&&(num==5||num==4||num==6||num==7)&&(num-4)==var12&&par1World.getBlockId(par2, par3-1, par4)==mod_pocketDim.linkDimDoorID)
			{
    	EntityPlayer player;
		if(par5Entity instanceof EntityPlayerMP)
    	{
    		
    		 player= (EntityPlayer) par5Entity;
    		 //int destinationID= dimHelper.instance.getDestIDFromCoords(par2, par3, par4, par1World);
    		 
    		 this.onPoweredBlockChange(par1World, par2, par3, par4, false);

    		 LinkData linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
  	        dimHelper.instance.teleportToPocket(par1World, linkData, player);
     			
    		
    	}
		}
    }
	
	 public int idPicked(World par1World, int par2, int par3, int par4)
	    {
	        return Item.doorSteel.itemID;
	    }
	    
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return (par1 & 8) != 0 ? 0 : (Item.doorSteel.itemID);
	    }
	
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        
       

        	
        	//System.out.println(String.valueOf(par1World.getBlockMetadata(par2, par3-1, par4)));

            int var10 = this.getFullMetadata(par1World, par2, par3, par4);
            int var11 = var10 & 7;
            var11 ^= 4;

            if ((var10 & 8) == 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, var11);
                par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            }
            else
            {
                par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var11);
                par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
            }

            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
            return true;
       
    }
	
	

}