package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockStainedHardenedClay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.rifts.destinations.PublicPocketDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class BlockDimensionalDoorIron extends BlockDimensionalDoor {

    public static final String ID = "dimensional_door";

    public BlockDimensionalDoorIron() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(2000.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.IRON_DOOR.getItemDropped(state, rand, fortune);
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        PublicPocketDestination destination = PublicPocketDestination.builder().build();
        rift.setDestination(destination);
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
