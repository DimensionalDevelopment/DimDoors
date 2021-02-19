package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface AutoSyncedAddon extends PocketAddon {
	static <T extends AutoSyncedAddon> List<T> readAutoSyncedAddonList(PacketByteBuf buf) throws IOException {
		List<T> addons = new ArrayList<>();
		int addonCount = buf.readInt();
		try {
			for (int i = 0; i < addonCount; i++) {
				addons.add((T) ((AutoSyncedAddon) PocketAddon.REGISTRY.get(buf.readIdentifier()).instance()).read(buf));
			}
		} catch (NullPointerException e) {
			throw new IOException(e);
		}
		return addons;
	}

	static PacketByteBuf writeAutoSyncedAddonList(PacketByteBuf buf, List<? extends AutoSyncedAddon> addons) throws IOException {
		buf.writeInt(addons.size());
		for (AutoSyncedAddon addon : addons) {
			buf.writeIdentifier(addon.getType().identifier());
			addon.write(buf);
		}
		return buf;
	}

	// you can generally use PacketByteBuf#readCompoundTag for reading
	AutoSyncedAddon read(PacketByteBuf buf) throws IOException;

	// you can generally use PacketByteBuf#writeCompoundTag for writing
	PacketByteBuf write(PacketByteBuf buf) throws IOException;
}
