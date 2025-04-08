package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

abstract public class AutoSyncedAddonContainer<T extends ContainedAddon & AutoSyncedAddon> extends AddonContainer<T> implements AutoSyncedAddon {
	@Override
	public AutoSyncedAddon read(RegistryFriendlyByteBuf buf) {
		id = ResourceLocation.tryParse(buf.readUtf());
		addons = AutoSyncedAddon.readAutoSyncedAddonList(buf);

		return this;
	}

	@Override
	public FriendlyByteBuf write(RegistryFriendlyByteBuf buf) {
		buf.writeUtf(id.toString());
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
		return buf;
	}
}
