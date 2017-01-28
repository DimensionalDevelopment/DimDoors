package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;

public class ItemDoorQuartz extends ItemDoor {

    public static final String ID = "itemDoorQuartz";

    public ItemDoorQuartz() {
        super(ModBlocks.blockDoorQuartz);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }
}
