package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;

public class ItemRiftGoggles extends ItemArmor
{

    public ItemRiftGoggles(int par1, int par2, int par3)
    {
    	  super(par1, EnumArmorMaterial.IRON, par1, par1);
          this.setCreativeTab(CreativeTabs.tabRedstone);
        //  this.setIconIndex(Item.doorWood.getIconFromDamage(0));
    }
    
   
}
