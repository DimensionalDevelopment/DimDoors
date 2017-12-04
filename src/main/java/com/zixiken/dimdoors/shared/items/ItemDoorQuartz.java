package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDoorQuartz;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;

public class ItemDoorQuartz extends ItemDoor {

    public ItemDoorQuartz() {
        super(ModBlocks.QUARTZ_DOOR);
        setUnlocalizedName(BlockDoorQuartz.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDoorQuartz.ID));
    }
}
