package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;

public class ItemDoorQuartz extends ItemDoor {
    public static final String ID = "itemDoorQuartz";

	public ItemDoorQuartz() {
		super(ModBlocks.blockDoorQuartz);
		setUnlocalizedName(ID);
	}
}
