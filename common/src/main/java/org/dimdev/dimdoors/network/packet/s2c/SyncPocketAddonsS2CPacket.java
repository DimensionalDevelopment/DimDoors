package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

public class SyncPocketAddonsS2CPacket implements CustomPacketPayload {
	public static CustomPacketPayload.Type<SyncPocketAddonsS2CPacket> TYPE = new Type<>(DimensionalDoors.id("sync_pocket_addons"));
	public static StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), SyncPocketAddonsS2CPacket::getWorld,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getGridSize,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getGridSize,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getGridSize,
			PocketAddon.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncPocketAddonsS2CPacket::getAddons,
			SyncPocketAddonsS2CPacket::new
	);

	private ResourceKey<Level> world;
	private int gridSize;
	private int pocketId;
	private int pocketRange;
	private List<PocketAddon> addons;

	@Environment(EnvType.CLIENT)
	public SyncPocketAddonsS2CPacket() {
	}

	public SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange, List<PocketAddon> addons) {
		this.world = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	public static void apply(SyncPocketAddonsS2CPacket value, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onSyncPocketAddons(value);
	}

	public int getGridSize() {
		return gridSize;
	}

	public int getPocketId() {
		return pocketId;
	}

	public int getPocketRange() {
		return pocketRange;
	}

	public List<PocketAddon> getAddons() {
		return addons;
	}

	public ResourceKey<Level> getWorld() {
		return world;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
