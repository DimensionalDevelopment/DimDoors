package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.IOException;

abstract public class AutoSyncedAddonContainer<T extends ContainedAddon & AutoSyncedAddon> extends AddonContainer<T> implements AutoSyncedAddon {
	@Override
	public AutoSyncedAddon read(PacketByteBuf buf) throws IOException {
		id = Identifier.tryParse(buf.readString());
		addons = AutoSyncedAddon.readAutoSyncedAddonList(buf);

		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeString(id.toString());
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
		return buf;
	}
}
