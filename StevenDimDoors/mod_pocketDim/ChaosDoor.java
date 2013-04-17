package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.blocks.dimDoor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

public class ChaosDoor extends dimDoor
{
	private Icon blockIconBottom;
	protected ChaosDoor(int par1, Material material) 
	{
		super(par1, Material.iron);
	//	this.blockIndexInTexture = 18;
     

	}
	
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
        this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");

    }

	@SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
       if(par1IBlockAccess.getBlockId(par2, par3-1, par4)==this.blockID)
       {
    	   return this.blockIcon;
       }
       else
       {
    	   return this.blockIconBottom;
       }
    }
	
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		if(!par1World.isRemote&&par1World.getBlockId(par2, par3-1, par4)==this.blockID)
		{		
			boolean newDim=false;
			
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)==null)
			{
				newDim=true;
			}
		
			if(newDim)
			{
				LinkData link = new LinkData(par1World.provider.dimensionId, mod_pocketDim.limboDimID, par2, par3, par4, par2, par3+500, par4, false);
				link.linkOrientation= par1World.getBlockMetadata(par2, par3-1, par4);
				dimHelper.instance.createLink(link);
			//	System.out.println(link.linkOrientation);

				
			}

			
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
			{
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation=par1World.getBlockMetadata(par2, par3-1, par4);
				

			}
		}
		
	}
	
	 
    //uses the rift rendering list to find a random destination for the player
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
     	
        int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
       
    	int num = par1World.getBlockMetadata(par2, par3-1, par4);
    	if(!par1World.isRemote&&(num==5||num==4||num==6||num==7)&&(num-4)==var12&&par1World.getBlockId(par2, par3-1, par4)==mod_pocketDim.chaosDoorID)
  		{
    		
    			this.onPoweredBlockChange(par1World, par2, par3, par4, false);
    			
    			boolean foundRandomDest=false;
    			
    			int i=0;
    			
	    		
	    		Random rand= new Random();
	    		
	    		while (!foundRandomDest&&i<100)
	    		{
	    			i++;
	    			
	    			LinkData link = (LinkData) dimHelper.instance.getRandomLinkData(false);
	    			
	    			if(link!=null)
	    			{
	    			
	    				if(!link.isLocPocket&&link.linkOrientation!=-10&&link.destDimID!=mod_pocketDim.limboDimID)
	    				{
	    					foundRandomDest=true;
	    					
	    					dimHelper.instance.teleportToPocket(par1World, new LinkData(link.destDimID,link.locDimID,link.destXCoord,link.destYCoord,link.destZCoord,link.locXCoord,link.locYCoord,link.locZCoord,link.isLocPocket), par5Entity);
	    					
	    					if(dimHelper.getWorld(link.locDimID)!=null)
	    					{
	    						if(dimHelper.getWorld(link.locDimID).isAirBlock(link.locXCoord,link.locYCoord,link.locZCoord))
	    						{
	    							dimHelper.getWorld(link.locDimID).setBlock(link.locXCoord,link.locYCoord,link.locZCoord, mod_pocketDim.blockRiftID);
	    						}
	    					}	    				    			
	    				}
	    			}    	   
	    		}   			   		
    		}
		}
    

}