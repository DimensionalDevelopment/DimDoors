package org.dimdev.dimdoors.world.pocket.type.addon;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

abstract public class AutoSyncedAddonContainer<T extends ContainedAddon & AutoSyncedAddon> extends AddonContainer<T> implements AutoSyncedAddon {
	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) throws IOException {
		id = ResourceLocation.tryParse(buf.readUtf());
		addons = AutoSyncedAddon.readAutoSyncedAddonList(buf);

		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeUtf(id.toString());
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
		return buf;
	}
}
