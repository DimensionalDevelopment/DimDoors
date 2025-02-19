package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AddonContainer<T extends ContainedAddon> implements PocketAddon {
	protected ResourceLocation id;
	protected List<T> addons = new ArrayList<>();

	public AddonContainer() {
	}

	public void setId(ResourceLocation id) {
		this.id = id;
	}

	public void addAll(Collection<T> addons) {
		this.addons.addAll(addons);
	}

	public void add(T addon) {
		this.addons.add(addon);
	}

//    @Override
//	public CompoundTag toNbt(CompoundTag nbt) {
//		PocketAddon.super.toNbt(nbt);
//
//		ListTag addonsTag = new ListTag();
//		for(T addon : addons) {
//			addonsTag.add(addon.toNbt(new CompoundTag()));
//		}
//		nbt.put("addons", addonsTag);
//
//		return null;
//	}

	@Override
	public ResourceLocation getId() {
		return id;
	}
}

