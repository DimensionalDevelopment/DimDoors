package org.dimdev.dimdoors.network.packet.s2c;

import com.mojang.datafixers.util.Function5;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SyncPocketAddonsS2CPacket {
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket, ResourceKey<Level>, Integer, Integer, Integer, List<PocketAddon>>composite(
			ResourceKey.streamCodec(Registries.DIMENSION), SyncPocketAddonsS2CPacket::getWorld,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getGridSize,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getPocketId,
			ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::getPocketRange,
			PocketAddon.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncPocketAddonsS2CPacket::getAddons, SyncPocketAddonsS2CPacket::new
	);

	public static final ResourceLocation ID = DimensionalDoors.id("sync_pocket_addons");

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

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ClientPacketHandler.getHandler().onSyncPocketAddons(this);
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
}
