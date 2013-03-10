package StevenDimDoors.mod_pocketDim;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemStableFabric extends Item
{
    private Material doorMaterial;

    public ItemStableFabric(int par1, int par2)
    {
    	  super(par1);
          this.setCreativeTab(CreativeTabs.tabRedstone);
        //  this.setIconIndex(Item.doorWood.getIconFromDamage(0));
          this.setCreativeTab(CreativeTabs.tabTransport);

    }
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    
    	System.out.println("Block metadata is "+par3World.getBlockMetadata(par4, par5, par6));
    	return true;
    }
    @Override
	 public String getTextureFile()
	 {
		 return "/PocketBlockTextures.png";
	 }
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	
    	
    
    	
    



    	
    }
}
