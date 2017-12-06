package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemRiftSignature extends Item {
    public static final String ID = "rift_signature";

    public ItemRiftSignature() {
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        translateAndAdd("info.rift_signature.unbound", tooltip);
    }
}
