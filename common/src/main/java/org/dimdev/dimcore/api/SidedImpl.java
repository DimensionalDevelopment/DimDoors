package org.dimdev.dimcore.api;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public abstract class SidedImpl<V extends SidedImpl<V, T>, T extends ModCommon<? super V>> implements ISided<V> {
    protected final T common;

    public SidedImpl(T common) {
        this.common = common;
    }

    @Override
    public String modId() {
        return common.getModId();
    }

    protected final Multimap<CreativeModeTab, ItemStack> APPENDS = MultimapBuilder.hashKeys().arrayListValues().build();

    @Override
    public void appendStack(CreativeModeTab tab, ItemStack item) {
        APPENDS.put(tab, item);
    }

    @Override
    public <T, V extends T> V register(ResourceKey<Registry<T>> key, String id, V obj) {
        return register(key, ResourceLocation.fromNamespaceAndPath(common.getModId(), id), obj);
    }

	@Override
	public <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, String id, V obj) {
		return registerHolder(key, ResourceLocation.fromNamespaceAndPath(common.getModId(), id), obj);
	}

	@Override
	public Path configPath() {
		return getConfigRoot().resolve(common.getModId() + "-config.json");
	}
}
