package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;

import java.util.Random;

public class BlockDimensionalDoorWood extends BlockDimensionalDoor { // TODO: all wood types

    public static final String ID = "oak_dimensional_door";

    public BlockDimensionalDoorWood() {
        super(Material.WOOD);
        setHardness(1.0F);
        setTranslationKey(ID);
        setRegistryName(DimDoors.getResource(ID));
    }

    @Override
    public Item getItem() {
        return ModItems.WOOD_DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.OAK_DOOR.getItemDropped(state, rand, fortune);
    }
}
