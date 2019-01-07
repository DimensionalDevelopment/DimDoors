package org.dimdev.dimdoors.shared.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModCreativeTabs {
    public static final CreativeTabs DIMENSIONAL_DOORS_CREATIVE_TAB = new CreativeTabs("dimensional_doors_creative_tab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR);
        }
    };
}
