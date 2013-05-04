package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.blocks.ExitDoor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
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
		super(par1, Material.grass);
	//	this.blockIndexInTexture = 18;
      

	}
	
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");

    }
	@SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
       
    	   return this.blockIcon;
       
       
    }
	 public boolean isCollidable()
	 {
	        return false;
	 }
	 
	 public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	 {
		 super.onBlockAdded(par1World, par2, par3, par4);
	    	this.updateAttatchedTile(par1World, par2, par3, par4);
	 }
	 
	 public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	    {
	        return null;
	    }

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
		
        int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
        int num = par1World.getBlockMetadata(par2, par3, par4);
     //   System.out.println("metadata "+num+" orientation "+var12);
    	
    	if(!par1World.isRemote&&(num)==var12||!par1World.isRemote&&!(par5Entity instanceof EntityPlayer))
			{
    
    		 
    		

    		 LinkData linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
    		 if(linkData!=null)
    		 {
    			 if(dimHelper.dimList.containsKey(linkData.destDimID))
    			 {
    				 dimHelper.instance.teleportToPocket(par1World, linkData, par5Entity);
    				 par1World.setBlock(par2, par3-1, par4, 0);
    				 par1World.setBlock(par2, par3, par4, mod_pocketDim.blockRiftID);

    			 }
    		 }
    		
        		  else
        		  {
        			  linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World);
            		  if(linkData!=null)
             		 {
             			 if(dimHelper.dimList.containsKey(linkData.destDimID))
             			 {
             				 dimHelper.instance.teleportToPocket(par1World, linkData, par5Entity);
             				 par1World.setBlock(par2, par3, par4, 0);
             				 par1World.setBlock(par2, par3+1, par4, mod_pocketDim.blockRiftID);

             			 }
             		 } 
        		  }

    		 }
     			
    		
    	}
		
    
	
	
	

		
	 public int idPicked(World par1World, int par2, int par3, int par4)
	    {
	        return 0;
	    }
	    
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return 0;
	    }
	    
	    
	    

	    /**
	     * A function to open a door.
	     */
	    
	    public int getRenderType()
	    {
	        return 8;
	    }


}