package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorChaos extends ItemDimensionalDoor { // TODO: remove this when converting to NBT setup, and just add creative menu item?

    public ItemDimensionalDoorChaos() {
        super(ModBlocks.IRON_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName("chaos_dimensional_door");
        setRegistryName(new ResourceLocation(DimDoors.MODID, "chaos_dimensional_door"));
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
