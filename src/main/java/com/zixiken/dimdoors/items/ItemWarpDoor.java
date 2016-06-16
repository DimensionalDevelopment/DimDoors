package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import net.minecraft.block.material.Material;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
        DimDoors.translateAndAdd("info.warpDoor",par3List);
	}
    
	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) DimDoors.warpDoor;
	}
}