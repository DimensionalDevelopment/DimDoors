package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockFabric;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockFabric extends ItemBlock {

    private final static String[] subNames = {"Reality", "Ancient", "Altered", "Unraveled", "Eternal"};

    public ItemBlockFabric() {
        super(ModBlocks.blockFabric);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
        setRegistryName(BlockFabric.ID);
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + subNames[this.getDamage(stack)];
    }
}
