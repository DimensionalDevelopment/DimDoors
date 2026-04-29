package org.dimdev.dimdoors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public abstract class SidedImpl implements ISided {
    protected final Multimap<CreativeModeTab, ItemStack> APPENDS = MultimapBuilder.hashKeys().arrayListValues().build();

    @Override
    public void appendStack(CreativeModeTab tab, ItemStack item) {
        APPENDS.put(tab, item);
    }
}
