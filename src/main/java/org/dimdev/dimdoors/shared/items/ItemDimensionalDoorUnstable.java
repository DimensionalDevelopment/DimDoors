package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

// TODO: remove this when converting to NBT setup, and just add creative menu item?
public class ItemDimensionalDoorUnstable extends ItemDimensionalDoor {

    public ItemDimensionalDoorUnstable() {
        super(ModBlocks.IRON_DIMENSIONAL_DOOR);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setRegistryName("unstable_dimensional_door");
        setRegistryName(new ResourceLocation(DimDoors.MODID, "unstable_dimensional_door"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }

    @Override
    public boolean canBePlacedOnRift() {
        return false;
    }
}
