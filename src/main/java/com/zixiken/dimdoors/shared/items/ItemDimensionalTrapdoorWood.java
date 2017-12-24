package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimensionalTrapdoorWood;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimensionalTrapdoorWood extends ItemDimensionalTrapdoor {

    public ItemDimensionalTrapdoorWood() {
        super(ModBlocks.WOOD_DIMENSIONAL_TRAPDOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalTrapdoorWood.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalTrapdoorWood.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        translateAndAdd("info.wood_dimensional_trapdoor", tooltip);
    }
}
