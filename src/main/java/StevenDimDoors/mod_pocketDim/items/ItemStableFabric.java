package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemStableFabric extends Item
{
	public ItemStableFabric(int par2)
	{
		super();
        this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}
}
