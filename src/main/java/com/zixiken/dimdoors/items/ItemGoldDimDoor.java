package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class ItemGoldDimDoor extends BaseItemDoor {
	public static final String ID = "itemGoldDimDoor";

	public ItemGoldDimDoor() {
  	    super(DimDoors.goldenDimensionalDoor, DimDoors.itemGoldenDoor);
        setUnlocalizedName(ID);
    }
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        DimDoors.translateAndAdd("info.goldDimDoor", tooltip);
	}

	@Override
	protected BaseDimDoor getDoorBlock() {return DimDoors.goldenDimensionalDoor;}
}
