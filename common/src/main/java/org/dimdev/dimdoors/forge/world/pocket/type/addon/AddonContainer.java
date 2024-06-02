package org.dimdev.dimdoors.forge.world.pocket.type.addon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		this.id = ResourceLocation.tryParse(nbt.getString("id"));

		if (nbt.contains("addons", Tag.TAG_LIST)) {
			for (Tag addonTag : nbt.getList("addons", Tag.TAG_COMPOUND)) {
				addons.add((T) PocketAddon.deserialize((CompoundTag) addonTag));
			}
		}

		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		PocketAddon.super.toNbt(nbt);

		ListTag addonsTag = new ListTag();
		for(T addon : addons) {
			addonsTag.add(addon.toNbt(new CompoundTag()));
		}
		nbt.put("addons", addonsTag);

		return null;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}
}

