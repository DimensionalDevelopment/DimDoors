package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemWorldThread extends Item
{
	public ItemWorldThread(int itemID)
	{
		super(itemID);
        this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}
	
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}
}
