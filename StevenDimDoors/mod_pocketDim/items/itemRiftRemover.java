package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemRiftRemover extends Item
{
    private Material doorMaterial;

    public itemRiftRemover(int par1, Material par2Material)
    {
    	 super(par1);
    	 this.setMaxStackSize(1);
    	// this.setTextureFile("/PocketBlockTextures.png");
         this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);

        // this.itemIcon=6;
         this.setMaxDamage(5);
         this.hasSubtypes=true;
    	 //TODO move to proxy
    }
    
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

    }
   
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
    	// adds effect if item has a link stored
    	
       return false;
    }
    
    public static MovingObjectPosition getBlockTarget(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float var4 = 1.0F;
        float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
        float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
        double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)var4;
        double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par2EntityPlayer.yOffset;
        double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)var4;
        Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = 5.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            var21 = 6;
        }
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return par1World.rayTraceBlocks_do_do(var13, var23, true, false);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	{		
    		MovingObjectPosition hit = 	this.getBlockTarget(par3EntityPlayer.worldObj, par3EntityPlayer, false );
    		if(hit!=null)
    		{
    			//System.out.println(hit.hitVec);
    			if(PocketManager.instance.removeRift(par2World, hit.blockX, hit.blockY, hit.blockZ, 1, par3EntityPlayer, par1ItemStack))
    			{
    				par3EntityPlayer.worldObj.playSoundAtEntity(par3EntityPlayer,"mods.DimDoors.sfx.riftClose", (float) .8, 1);

    			}
    			
    		}
    	//	dimHelper.removeRift( par3World,  par4,  par5,  par6,  range,  par2EntityPlayer,  par1ItemStack);
    		
    		
    	}
    	return par1ItemStack;
    }
    
    
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	
    
    		par3List.add("Use near exposed rift");
    		par3List.add ("to remove it and");
    		par3List.add("any nearby rifts");
    	


    	
    }
    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
    {
    	if(!par2World.isRemote)
    	{
    		/**
    		//creates the first half of the link on item creation
    		int key= dimHelper.instance.createUniqueInterDimLinkKey();
    		LinkData linkData= new LinkData(par2World.provider.dimensionId,MathHelper.floor_double(par3EntityPlayer.posX),MathHelper.floor_double(par3EntityPlayer.posY),MathHelper.floor_double(par3EntityPlayer.posZ));
    		System.out.println(key);

    		dimHelper.instance.interDimLinkList.put(key, linkData);
    		par1ItemStack.setItemDamage(key);
    		**/
    	}
    }
}
