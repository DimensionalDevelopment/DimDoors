package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemDimDoorPersonal extends ItemDoorBase {
	public static final String ID = "itemDimDoorQuartz";

	public ItemDimDoorPersonal() {
  	    super(ModBlocks.blockDimDoorPersonal, ModItems.itemDoorQuartz);
        setUnlocalizedName(ID);
    }

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		translateAndAdd("info.personalDimDoor", tooltip);
	}

	@Override
	protected BlockDimDoorBase getDoorBlock() {return ModBlocks.blockDimDoorPersonal;}
}