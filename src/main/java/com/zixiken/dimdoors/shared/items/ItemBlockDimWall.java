package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimWall;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDimWall extends ItemBlock {

    private final static String[] subNames = {"", "Ancient", "Altered"};

    public ItemBlockDimWall() {
        super(ModBlocks.blockDimWall);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
        setRegistryName(BlockDimWall.ID);
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + subNames[this.getDamage(stack)];
    }
}
