package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface AutoSyncedAddon extends PocketAddon {
	static <T extends AutoSyncedAddon> List<T> readAutoSyncedAddonList(FriendlyByteBuf buf) throws IOException {
		List<T> addons = new ArrayList<>();
		int addonCount = buf.readInt();
		try {
			for (int i = 0; i < addonCount; i++) {
				addons.add((T) ((AutoSyncedAddon) PocketAddon.REGISTRY.get(buf.readResourceLocation()).instance()).read(buf));
			}
		} catch (NullPointerException e) {
			throw new IOException(e);
		}
		return addons;
	}

	static FriendlyByteBuf writeAutoSyncedAddonList(FriendlyByteBuf buf, List<? extends AutoSyncedAddon> addons) throws IOException {
		buf.writeInt(addons.size());
		for (AutoSyncedAddon addon : addons) {
			buf.writeResourceLocation(addon.getType().identifier());
			addon.write(buf);
		}
		return buf;
	}

	// you can generally use FriendlyByteBuf#readCompoundTag for reading
	AutoSyncedAddon read(FriendlyByteBuf buf) throws IOException;

	// you can generally use FriendlyByteBuf#writeCompoundTag for writing
	FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException;
}
