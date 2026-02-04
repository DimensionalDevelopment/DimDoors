package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.util.List;

public class SyncPocketAddonsS2CPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("sync_pocket_addons");
	public static final CustomPacketPayload.Type<SyncPocketAddonsS2CPacket> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = StreamCodec.of((object, object2) -> object2.write(object), SyncPocketAddonsS2CPacket::new);

	private ResourceKey<Level> world;
	private int gridSize;
	private int pocketId;
	private int pocketRange;
	private List<AutoSyncedAddon> addons;

	public SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) {
		this.world = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	public SyncPocketAddonsS2CPacket(FriendlyByteBuf buf) {
		this(buf.readResourceKey(Registries.DIMENSION),
		buf.readInt(),
		buf.readInt(),
		buf.readInt(),
		AutoSyncedAddon.readAutoSyncedAddonList(buf));
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeResourceKey(world);
		buf.writeInt(gridSize);
		buf.writeInt(pocketId);
		buf.writeInt(pocketRange);
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
		return buf;
	}

	public static void apply(SyncPocketAddonsS2CPacket packet, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onSyncPocketAddons(packet);
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

	public List<AutoSyncedAddon> getAddons() {
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