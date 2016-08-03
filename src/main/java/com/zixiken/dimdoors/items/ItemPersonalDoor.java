package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemPersonalDoor extends BaseItemDoor {
	public static final String ID = "itemQuartzDimDoor";

	public ItemPersonalDoor() {
  	    super(DimDoors.personalDimDoor, DimDoors.itemQuartzDoor);
        setUnlocalizedName(ID);
    }

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		DimDoors.translateAndAdd("info.personalDimDoor", tooltip);
	}

	@Override
	protected BaseDimDoor getDoorBlock() {return (BaseDimDoor) DimDoors.personalDimDoor;}
}