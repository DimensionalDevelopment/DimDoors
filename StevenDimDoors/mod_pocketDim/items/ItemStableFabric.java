package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
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
        //  this.setIconIndex(Item.doorWood.getIconFromDamage(0));
          this.setCreativeTab(CreativeTabs.tabTransport);

    }
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

    }
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    
    	System.out.println("Block metadata is "+par3World.getBlockMetadata(par4, par5, par6));
    	System.out.println("Block texture data is "+Block.blocksList[par3World.getBlockId(par4, par5, par6)].getBlockTexture(par3World,par4, par5, par6,par7).getIconName());
    	System.out.println("Block name is is "+Block.blocksList[par3World.getBlockId(par4, par5, par6)].getUnlocalizedName2());

    	return true;
    }
    
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	
    	
    
    	
    



    	
    }
}
