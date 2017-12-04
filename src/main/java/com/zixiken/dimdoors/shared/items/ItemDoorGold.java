package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDoorGold;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;

public class ItemDoorGold extends ItemDoor {

    public ItemDoorGold() {
        super(ModBlocks.GOLD_DOOR);
        setMaxStackSize(16);
        setUnlocalizedName(BlockDoorGold.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDoorGold.ID));
    }
}
