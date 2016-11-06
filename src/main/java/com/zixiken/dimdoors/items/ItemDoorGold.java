package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;

public class ItemDoorGold extends ItemDoor {
	public static final String ID = "itemDoorGold";

	public ItemDoorGold() {
		super(ModBlocks.blockDoorGold);
		setMaxStackSize(16);
        setUnlocalizedName(ID);
	}
}
