package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorIron;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.shared.rifts.targets.PublicPocketTarget;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorIron extends ItemDimensionalDoor {

    public ItemDimensionalDoorIron() {
        super(ModBlocks.IRON_DIMENSIONAL_DOOR);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorIron.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorIron.ID));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        PublicPocketTarget destination = PublicPocketTarget.builder().build();
        rift.setDestination(destination);
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
