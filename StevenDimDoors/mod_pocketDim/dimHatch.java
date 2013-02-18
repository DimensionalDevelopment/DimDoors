package StevenDimDoors.mod_pocketDim;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class dimHatch extends BlockTrapDoor
{

	protected dimHatch(int par1,int par2, Material par2Material) 
	{
		super(par1, Material.iron);
        this.setCreativeTab(CreativeTabs.tabTransport);
       // this.setTextureFile("/PocketBlockTextures.png");
	//	 this.blockIndexInTexture =  16;
	}

	
	 @Override
	 public String getTextureFile()
	 {
		 this.blockIndexInTexture =  16;
		 return "/PocketBlockTextures.png";
	 }
	 public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	    {
	       
	        {
	            int var10 = par1World.getBlockMetadata(par2, par3, par4);
	            par1World.setBlockMetadataWithNotify(par2, par3, par4, var10 ^ 4);
	            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
	            return true;
	        }
	    }
	 //Teleports the player to the exit link of that dimension, assuming it is a pocket
	 public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
	 {	 
	    
	    int num = par1World.getBlockMetadata(par2, par3, par4);
	    
	   	if(!par1World.isRemote&&(num>3&&num<8||num>11)&&par1World.provider instanceof pocketProvider)
		{
	    	EntityPlayerMP playerMP;
	    	if(par5Entity instanceof EntityPlayerMP)
	    	{
	    		playerMP= (EntityPlayerMP) par5Entity;
	    		this.onPoweredBlockChange(par1World, par2, par3, par4, false);
	    		 
	   			DimData dimData = (DimData) dimHelper.instance.dimList.get(par1World.provider.dimensionId);
	    			
	    		LinkData exitLink=dimData.exitDimLink;
	    		 
	    		dimHelper.instance.teleportToPocket(par1World, exitLink, playerMP);
	
	    	}
		}
	 }

	    public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
	    {
	        int var6 = par1World.getBlockMetadata(par2, par3, par4);
	        boolean var7 = (var6 & 4) > 0;

	        if (var7 != par5)
	        {
	            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 ^ 4);
	            par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
	        }
	    }
}