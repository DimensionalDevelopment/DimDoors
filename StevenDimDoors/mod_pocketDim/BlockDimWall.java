package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

public class BlockDimWall extends Block
{
	
	protected BlockDimWall(int i, int j, Material par2Material) 
	{
		 super(i, Material.ground);
	        setTickRandomly(true);
	        this.setCreativeTab(CreativeTabs.tabBlock);
	    
	       
	       
	        
	}
	
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
    }
	
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}
    
    public int quantityDropped(Random par1Random)
    {
        
        
            return 0;
        
    }
   
    /**
     * replaces the block clicked with the held block, instead of placing the block on top of it. Shift click to disable. 
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        if(!par1World.isRemote&&entityPlayer.getCurrentEquippedItem()!=null)
        {
        
        	if(entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)
        	{
        		if(!entityPlayer.capabilities.isCreativeMode)
        		{
        			entityPlayer.getCurrentEquippedItem().stackSize--;
        		}
        		par1World.setBlock(par2, par3, par4,  entityPlayer.getCurrentEquippedItem().itemID, entityPlayer.getCurrentEquippedItem().getItemDamage(),0);
        		return true;
        	}
        	
        }
        if(par1World.isRemote&&entityPlayer.getCurrentEquippedItem()!=null)
        {
        
        	if(entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)
        	{      		
        		return true;
        	}
        	
        }
	
	return false;

    }
   

}
