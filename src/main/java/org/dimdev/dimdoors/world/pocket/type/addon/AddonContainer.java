package org.dimdev.dimdoors.world.pocket.type.addon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

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

		if (nbt.contains("addons", NbtType.LIST)) {
			for (Tag addonTag : nbt.getList("addons", NbtType.COMPOUND)) {
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

