package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorPersonal;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorPersonal extends ItemDoorBase {

    public ItemDimDoorPersonal() {
        super(ModBlocks.PERSONAL_DIMENSIONAL_DOOR, ModItems.QUARTZ_DOOR);
        setUnlocalizedName(BlockDimDoorPersonal.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimDoorPersonal.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        translateAndAdd("info.quartz_dimensional_door", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.PERSONAL_DIMENSIONAL_DOOR;
    }
}
