package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class ItemWarpDoor extends BaseItemDoor {
	public static final String ID = "itemDimDoorWarp";

	public ItemWarpDoor() {
		super(DimDoors.warpDoor, (ItemDoor)Items.iron_door);
        setUnlocalizedName(ID);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        DimDoors.translateAndAdd("info.warpDoor",tooltip);
	}
    
	@Override
	protected BaseDimDoor getDoorBlock() {return (BaseDimDoor) DimDoors.warpDoor;}
}