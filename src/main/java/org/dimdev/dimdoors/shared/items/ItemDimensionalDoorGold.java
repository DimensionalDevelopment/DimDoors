package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorGold;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.targets.RandomTarget;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ItemDimensionalDoorGold extends ItemDimensionalDoor {

    public ItemDimensionalDoorGold() {
        super(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setTranslationKey(BlockDimensionalDoorGold.ID);
        setRegistryName(DimDoors.getResource(BlockDimensionalDoorGold.ID));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        rift.setProperties(LinkProperties.builder()
                .groups(new HashSet<>(Arrays.asList(0, 1)))
                .linksRemaining(1).build());
        rift.setDestination(RandomTarget.builder()
                .acceptedGroups(Collections.singleton(0))
                .coordFactor(1)
                .negativeDepthFactor(10000)
                .positiveDepthFactor(80)
                .weightMaximum(100)
                .noLink(false)
                .noLinkBack(false)
                .newRiftWeight(1).build());
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
