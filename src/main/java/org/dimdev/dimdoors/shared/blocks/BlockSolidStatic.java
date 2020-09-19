package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;
import org.dimdev.dimdoors.shared.items.ModItems;

import java.util.Random;

public class BlockSolidStatic extends Block {


    public static final String ID = "solid_static";

    public BlockSolidStatic(Material material) {
        super(material);
        this.setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        this.setTranslationKey(ID);
        this.setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        this.setHardness(7.0F);
        this.setResistance(25.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setSoundType(SoundType.SAND);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return ModItems.WORLD_THREAD;
    }

    @Override
    public int quantityDropped(Random rand){
        int max = 3;
        int min = 0;
        return rand.nextInt(max) + min;
    }
}
