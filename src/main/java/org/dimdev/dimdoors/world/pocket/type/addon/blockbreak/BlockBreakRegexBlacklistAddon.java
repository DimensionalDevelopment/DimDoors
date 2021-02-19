package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.ContainedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.io.IOException;

// TODO
public class BlockBreakRegexBlacklistAddon implements AutoSyncedAddon, ContainedAddon { //TODO
	@Override
	public AutoSyncedAddon read(PacketByteBuf buf) throws IOException {
		this.fromTag(buf.readCompoundTag());
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeCompoundTag(this.toTag(new CompoundTag()));
		return buf;
	}

	@Override
	public Identifier getContainerId() {
		return BlockBreakContainer.ID;
	}

	@Override
	public PocketAddon fromTag(CompoundTag tag) {
		return null;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return null;
	}

	@Override
	public Identifier getId() {
		return null;
	}
}
