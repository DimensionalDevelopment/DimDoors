package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDoorGold;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;

public class ItemDoorGold extends ItemDoor {

    public ItemDoorGold() {
        super(ModBlocks.GOLD_DOOR);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDoorGold.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDoorGold.ID));
    }
}
