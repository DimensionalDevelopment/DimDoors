package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimensionalDoorWarp;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorWarp extends ItemDoor {

    public ItemDimDoorWarp() {
        super(ModBlocks.WARP_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorWarp.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorWarp.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        translateAndAdd("info.warp_dimensional_door", tooltip);
    }
}
