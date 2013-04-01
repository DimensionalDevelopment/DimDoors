package StevenDimDoors.mod_pocketDim;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;

public class ItemRiftGoggles extends ItemArmor
{
    private Material doorMaterial;

    public ItemRiftGoggles(int par1, int par2, int par3)
    {
    	  super(par1, EnumArmorMaterial.IRON, par1, par1);
          this.setCreativeTab(CreativeTabs.tabRedstone);
        //  this.setIconIndex(Item.doorWood.getIconFromDamage(0));
    }
    
   
}
