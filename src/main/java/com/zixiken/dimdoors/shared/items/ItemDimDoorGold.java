package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorGold;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorGold extends ItemDoor {

    public ItemDimDoorGold() {
        super(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        setUnlocalizedName(BlockDimDoorGold.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimDoorGold.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        translateAndAdd("info.gold_dimensional_door", tooltip);
    }
}
