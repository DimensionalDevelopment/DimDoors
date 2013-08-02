package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ExitDoor extends dimDoor
{

	private Icon blockIconBottom;
	public ExitDoor(int par1, Material par2Material) 
	{
		
		super(par1, Material.wood);
       

		// TODO Auto-generated constructor stub
	}

	
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
        this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");
    
    }
	
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		
		
		if(!par1World.isRemote&&par1World.getBlockId(par2, par3-1, par4)==this.blockID)
		{
			
		
			int locDimID=par1World.provider.dimensionId;
			
			if(dimHelper.instance.dimList.containsKey(locDimID)&&dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)==null)
			{
				DimData dimData = dimHelper.instance.getDimData(locDimID);
				int ExitDimID = dimData.exitDimLink.destDimID;
	   			if(dimHelper.instance.getDimData(par1World.provider.dimensionId).isPocket)
	   			{
	   				int yCoord=yCoordHelper.getFirstUncovered(ExitDimID, par2, par3, par4);
					  
					
					dimHelper.instance.createLink(locDimID, ExitDimID, par2, par3, par4, par2, yCoord, par4,par1World.getBlockMetadata(par2, par3-1, par4));
					dimHelper.instance.createLink(ExitDimID, locDimID, par2, yCoord, par4, par2, par3, par4,
							BlockRotator.transformMetadata(par1World.getBlockMetadata(par2, par3 - 1, par4), 2, Block.doorWood.blockID));
	   			}

	   				
				
	   		/**
	   		
	   			if(dimHelper.instance.getDimDepth(locDimID)==1)			
	   			{
	   				//System.out.println("exitToOverowrld from "+String.valueOf(locDimID));
	   				
				   	int yCoord=yCoordHelper.getFirstUncovered(ExitDimID, par2, par3, par4);
				  
						
					dimHelper.instance.createLink(locDimID, ExitDimID, par2, par3, par4, par2, yCoord, par4,par1World.getBlockMetadata(par2, par3-1, par4));
					dimHelper.instance.createLink(ExitDimID, locDimID, par2, yCoord, par4, par2, par3, par4,dimHelper.instance.flipDoorMetadata(par1World.getBlockMetadata(par2, par3-1, par4)));

					


	   			}
	   			else if(dimHelper.instance.getDimData(par1World.provider.dimensionId).isPocket)
	   			{
	   				//System.out.println("Created new dim from "+String.valueOf(par1World.provider.dimensionId));

	   				LinkData link = new LinkData(par1World.provider.dimensionId, 0, par2, par3, par4, par2, par3, par4, true, par1World.getBlockMetadata(par2, par3-1, par4));
					dimHelper.instance.createPocket(link,false, false);

				
	   				
	   			//	dimHelper.instance.generatePocket(dimHelper.getWorld(destDimID), par2, par3, par4,par1World.getBlockMetadata(par2, par3-1, par4));


	   			}
	   			**/
   		 	
			}
			else if (dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
			{
				
				//System.out.println("RiftPresent at "+String.valueOf(par1World.provider.dimensionId));
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation=par1World.getBlockMetadata(par2, par3-1, par4);
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).hasGennedDoor=false;


				

				

			}
			
		
		
		}
		
		//this.onPoweredBlockChange(par1World, par2, par3, par4, false);
		
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
	
	 public int idPicked(World par1World, int par2, int par3, int par4)
	    {
	        return Item.doorWood.itemID;
	    }
	    
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return (par1 & 8) != 0 ? 0 : (Item.doorWood.itemID);
	    }
	
	
	

}