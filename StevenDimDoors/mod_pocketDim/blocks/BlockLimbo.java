package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLimbo extends Block
{
	Random rand= new Random();
	Icon blockIcon0;
	Icon blockIcon1;
	Icon blockIcon2;
	Icon blockIcon3;
	
	public BlockLimbo(int i, int j, Material par2Material) 
	{
		 super(i, Material.ground);
	        setTickRandomly(false);
	         this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	       
	       
	        
	}
	 @SideOnly(Side.CLIENT)

	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	    {
	        return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
	    }
	 public void registerIcons(IconRegister par1IconRegister)
	    {
	        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	        this.blockIcon0 = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+0);
	        this.blockIcon1 = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+1);
	        this.blockIcon2 = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+2);
	        this.blockIcon3 = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+3);

	    }
	 
	 public Icon getIcon(int par1, int par2)
	    {
		 /**
		 switch(par2)
		 {
		 case 0: return this.blockIcon0;
		 case 1: return this.blockIcon1;
				 
		 case 2: return this.blockIcon2;
		 case 3: return this.blockIcon3;
		 }
		 **/
			 
		
	        return this.blockIcon;
	    }
	
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}
    
    //part of the decay mech, if a block has fallen onto it, when it turns, it makes sure any block above it gets added too. 
    @Override
    
    public int quantityDropped(Random par1Random)
    {
        
        
            return 1;
        
    }
    
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
       
	return false;

    }
    //if a block lands on it and its gravel, adds it to the decay list
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) 
    {
    	
    	if(par1World.getBlockId(par2, par3+1, par4)==Block.gravel.blockID)
    	{
    		 Point3D point = new Point3D(par2,par3+1,par4);
             dimHelper.blocksToDecay.add(point);
    	}
    	
    }
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) 
    {
    	if(par1World.getBlockId(par2, par3+1, par4)==Block.gravel.blockID)
    	{
    		 Point3D point = new Point3D(par2,par3+1,par4);
             dimHelper.blocksToDecay.add(point);
    	}
    }
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
    //	par1World.setBlockMetadataWithNotify(par2, par3, par4, this.rand.nextInt(4), 0);
    }

   
   //TODO set render color!!
}
