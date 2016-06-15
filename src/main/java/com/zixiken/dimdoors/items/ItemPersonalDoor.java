package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class ItemPersonalDoor extends BaseItemDoor {
	public static final String ID = "itemQuartzDimDoor";

	public ItemPersonalDoor() {
  	    super(mod_pocketDim.personalDimDoor, mod_pocketDim.itemQuartzDoor);
        setUnlocalizedName(ID);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		mod_pocketDim.translateAndAdd("info.personalDimDoor", par3List);
	}

	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) mod_pocketDim.personalDimDoor;
	}
}