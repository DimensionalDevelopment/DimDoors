package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemArmor;

public class ItemRiftGoggles extends ItemArmor
{

    public ItemRiftGoggles(int par2, int par3)
    {
    	  super(ArmorMaterial.IRON, par2, par3);
          this.setCreativeTab(CreativeTabs.tabRedstone);
        //  this.setIconIndex(Item.doorWood.getIconFromDamage(0));
    }
    
   
}
