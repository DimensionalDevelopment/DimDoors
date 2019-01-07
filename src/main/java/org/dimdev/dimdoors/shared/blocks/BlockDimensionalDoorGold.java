package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.state.IBlockState;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class BlockDimensionalDoorGold extends BlockDimensionalDoor {

    public static final String ID = "gold_dimensional_door";

    public BlockDimensionalDoorGold() {
        super(Material.IRON);
        setHardness(1.0F);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.GOLD_DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModBlocks.GOLD_DOOR.getItemDropped(state, rand, fortune);
    }
}
