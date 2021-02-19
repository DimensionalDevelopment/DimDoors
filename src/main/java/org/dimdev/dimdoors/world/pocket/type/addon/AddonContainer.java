package org.dimdev.dimdoors.world.pocket.type.addon;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AddonContainer<T extends ContainedAddon> implements PocketAddon {
	protected Identifier id;
	protected List<T> addons = new ArrayList<>();

	public AddonContainer() {
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public void addAll(Collection<T> addons) {
		this.addons.addAll(addons);
	}

	public void add(T addon) {
		this.addons.add(addon);
	}

	@Override
	public PocketAddon fromTag(CompoundTag tag) {
		this.id = Identifier.tryParse(tag.getString("id"));

		if (tag.contains("addons", NbtType.LIST)) {
			for (Tag addonTag : tag.getList("addons", NbtType.COMPOUND)) {
				addons.add((T) PocketAddon.deserialize((CompoundTag) addonTag));
			}
		}

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		PocketAddon.super.toTag(tag);

		ListTag addonsTag = new ListTag();
		for(T addon : addons) {
			addonsTag.add(addon.toTag(new CompoundTag()));
		}
		tag.put("addons", addonsTag);

		return null;
	}

	@Override
	public Identifier getId() {
		return id;
	}
}

