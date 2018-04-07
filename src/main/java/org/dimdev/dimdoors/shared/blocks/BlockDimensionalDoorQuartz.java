package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;

import java.util.Random;

public class BlockDimensionalDoorQuartz extends BlockDimensionalDoor {

    public static final String ID = "quartz_dimensional_door";

    public BlockDimensionalDoorQuartz() {
        super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.QUARTZ_DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModBlocks.QUARTZ_DOOR.getItemDropped(state, rand, fortune);
    }
}
