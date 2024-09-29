package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncPocketAddonsS2CPacket> TYPE = new CustomPacketPayload.Type<>(DimensionalDoors.id("sync_pocket_addons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncPocketAddonsS2CPacket::write, SyncPocketAddonsS2CPacket::new);

	private SyncPocketAddonsS2CPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readResourceKey(Registries.DIMENSION),
		buf.readInt(),
		buf.readInt(),
		buf.readInt(),
		AutoSyncedAddon.readAutoSyncedAddonList(buf));
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeResourceKey(world);
		buf.writeInt(gridSize);
		buf.writeInt(pocketId);
		buf.writeInt(pocketRange);
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
