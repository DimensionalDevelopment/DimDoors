package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;

import java.util.Random;

public class BlockDimensionalTrapdoorWood extends BlockDimensionalTrapdoor { // TODO: iron trapdoor, all wood types

    public static final String ID = "dimensional_trapdoor";

    public BlockDimensionalTrapdoorWood() {
        super(Material.WOOD);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setTranslationKey(ID);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setHardness(1.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.TRAPDOOR.getItemDropped(state, rand, fortune);
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
