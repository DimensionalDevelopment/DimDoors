package org.dimdev.dimdoors.world.pocket.type.addon;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
	public PocketAddon fromTag(NbtCompound tag) {
		this.id = Identifier.tryParse(tag.getString("id"));

		if (tag.contains("addons", NbtType.LIST)) {
			for (NbtElement addonTag : tag.getList("addons", NbtType.COMPOUND)) {
				addons.add((T) PocketAddon.deserialize((NbtCompound) addonTag));
			}
		}

		return this;
	}

	@Override
	public NbtCompound toTag(NbtCompound tag) {
		PocketAddon.super.toTag(tag);

		NbtList addonsTag = new NbtList();
		for(T addon : addons) {
			addonsTag.add(addon.toTag(new NbtCompound()));
		}
		tag.put("addons", addonsTag);

		return null;
	}

	@Override
	public Identifier getId() {
		return id;
	}
}

