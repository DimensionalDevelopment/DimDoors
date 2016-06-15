package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class ItemWarpDoor extends BaseItemDoor
{
	public ItemWarpDoor(Material material, ItemDoor door)
	{
		super(material, door);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
        mod_pocketDim.translateAndAdd("info.warpDoor",par3List);
	}
    
	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) mod_pocketDim.warpDoor;
	}
}