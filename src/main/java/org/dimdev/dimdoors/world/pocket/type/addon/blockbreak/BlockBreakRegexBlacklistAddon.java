package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.minecraft.nbt.NbtCompound;
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
		this.fromNbt(buf.readNbt());
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeNbt(this.toNbt(new NbtCompound()));
		return buf;
	}

	@Override
	public Identifier getContainerId() {
		return BlockBreakContainer.ID;
	}

	@Override
	public PocketAddon fromNbt(NbtCompound nbt) {
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
