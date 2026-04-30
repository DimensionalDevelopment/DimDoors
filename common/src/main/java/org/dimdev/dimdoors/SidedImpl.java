package org.dimdev.dimdoors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public abstract class SidedImpl implements ISided {
    protected final Multimap<CreativeModeTab, ItemStack> APPENDS = MultimapBuilder.hashKeys().arrayListValues().build();

    @Override
    public void appendStack(CreativeModeTab tab, ItemStack item) {
        APPENDS.put(tab, item);
    }
}
