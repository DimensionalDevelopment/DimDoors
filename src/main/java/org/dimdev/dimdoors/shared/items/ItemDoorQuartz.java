package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDoorQuartz;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;

public class ItemDoorQuartz extends ItemDoor {

    public ItemDoorQuartz() {
        super(ModBlocks.QUARTZ_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDoorQuartz.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDoorQuartz.ID));
    }
}
