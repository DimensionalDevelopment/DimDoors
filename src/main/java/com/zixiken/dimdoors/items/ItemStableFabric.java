package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemStableFabric extends Item {
	public static final String ID = "itemStableFabric";

	public ItemStableFabric() {
		super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
	}
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(DimDoors.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}
}
