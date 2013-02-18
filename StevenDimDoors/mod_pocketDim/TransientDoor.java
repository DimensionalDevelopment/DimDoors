package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransientDoor extends ExitDoor
{
	
	protected TransientDoor(int par1, Material material) 
	{
		super(par1, Material.air);
	//	this.blockIndexInTexture = 18;
        this.setTextureFile("/PocketBlockTextures.png");

	}
	 public boolean isCollidable()
	 {
	        return false;
	 }
	 
	    public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	    {
	    	this.updateAttatchedTile(par1World, par2, par3, par4);
	    }

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
		
        int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
        int num = par1World.getBlockMetadata(par2, par3, par4);
     //   System.out.println("metadata "+num+" orientation "+var12);
    	
    	if(!par1World.isRemote&&(num)==var12)
			{
    	EntityPlayer player;
		if(par5Entity instanceof EntityPlayerMP)
    	{
    		
    		 player= (EntityPlayer) par5Entity;
    		 //int destinationID= dimHelper.instance.getDestIDFromCoords(par2, par3, par4, par1World);
    		 
    		

    		 LinkData linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
    		 if(linkData!=null)
    		 {
    			 if(dimHelper.dimList.containsKey(linkData.destDimID))
    			 {
    				 dimHelper.instance.teleportToPocket(par1World, linkData, player);
    				 par1World.setBlockWithNotify(par2, par3, par4, 0);
    			 }
    		 }
    		
        		  else
        		  {
        			  linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World);
            		  if(linkData!=null)
             		 {
             			 if(dimHelper.dimList.containsKey(linkData.destDimID))
             			 {
             				 dimHelper.instance.teleportToPocket(par1World, linkData, player);
             				 par1World.setBlockWithNotify(par2, par3, par4, 0);
             			 }
             		 } 
        		  }

    		 }
     			
    		
    	}
		
    }
	public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
    {
      
    }
	
	
	 
	  public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	    {
	        return super.getCollisionBoundingBoxFromPool(par1World, 0, 0, 0);
	    }

		
	 public int idPicked(World par1World, int par2, int par3, int par4)
	    {
	        return 0;
	    }
	    
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return 0;
	    }
	    
	    
	    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	    {
	        
	    	return false;
	       
	    }

	    /**
	     * A function to open a door.
	     */
	    
	    public int getRenderType()
	    {
	        return 8;
	    }


}