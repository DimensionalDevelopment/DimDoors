package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.ContainedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

// TODO
public class BlockBreakRegexBlacklistAddon implements AutoSyncedAddon, ContainedAddon { //TODO
	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) {
		this.fromNbt(buf.readNbt());
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeNbt(this.toNbt(new CompoundTag()));
		return buf;
	}

	@Override
	public ResourceLocation getContainerId() {
		return BlockBreakContainer.ID;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		return null;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return null;
	}

	@Override
	public ResourceLocation getId() {
		return null;
	}
}
