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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
        DimDoors.translateAndAdd("info.goldDimDoor", par3List);
	}

	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) DimDoors.goldenDimensionalDoor;
	}
}
