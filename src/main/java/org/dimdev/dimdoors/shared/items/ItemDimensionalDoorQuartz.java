package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorQuartz;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivateDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivatePocketExitDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;

public class ItemDimensionalDoorQuartz extends ItemDimensionalDoor {

    public ItemDimensionalDoorQuartz() {
        super(ModBlocks.PERSONAL_DIMENSIONAL_DOOR);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorQuartz.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorQuartz.ID));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        if (rift.getWorld().provider instanceof WorldProviderPersonalPocket) {
            rift.setDestination(new PrivatePocketExitDestination()); // exit
        } else {
            rift.setDestination(new PrivateDestination()); // entrances
        }
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
