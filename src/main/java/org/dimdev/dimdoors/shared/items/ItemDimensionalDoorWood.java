package org.dimdev.dimdoors.shared.items;

import java.util.Collections;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorWood;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.shared.rifts.targets.RandomTarget;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorWood extends ItemDimensionalDoor {

    public ItemDimensionalDoorWood() {
        super(ModBlocks.WARP_DIMENSIONAL_DOOR);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorWood.ID));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        rift.setDestination(RandomTarget.builder()
                .acceptedGroups(Collections.singleton(0))
                .coordFactor(1)
                .negativeDepthFactor(80)
                .positiveDepthFactor(Double.MAX_VALUE)
                .weightMaximum(100)
                .noLink(false).newRiftWeight(0).build());
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
