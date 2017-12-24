package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockFabric;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemFabric extends ItemBlock {

    private static final String[] subNames = {"_reality", "_ancient", "_altered", "_ancient_altered", "_unraveled", "_eternal"};

    public ItemFabric() {
        super(ModBlocks.FABRIC);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockFabric.ID);
        setRegistryName(BlockFabric.ID);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + subNames[getDamage(stack)];
    }
}
