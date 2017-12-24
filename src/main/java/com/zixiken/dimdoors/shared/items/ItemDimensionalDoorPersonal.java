package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimensionalDoorPersonal;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDimensionalDoorPersonal extends ItemDimensionalDoor {

    public ItemDimensionalDoorPersonal() {
        super(ModBlocks.PERSONAL_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorPersonal.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorPersonal.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        I18nUtils.translateAndAdd("info.quartz_dimensional_door", tooltip);
    }
}
