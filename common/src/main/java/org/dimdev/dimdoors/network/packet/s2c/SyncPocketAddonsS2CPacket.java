package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.forge.world.pocket.type.addon.AutoSyncedAddon;

import java.util.List;
import java.util.function.Supplier;

public class SyncPocketAddonsS2CPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("sync_pocket_addons");

	private ResourceKey<Level> world;
	private int gridSize;
	private int pocketId;
	private int pocketRange;
	private List<AutoSyncedAddon> addons;

	@Environment(EnvType.CLIENT)
	public SyncPocketAddonsS2CPacket() {
	}

	public SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) {
		this.world = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	public SyncPocketAddonsS2CPacket(FriendlyByteBuf buf) {
		this(buf.readResourceKey(Registry.DIMENSION_REGISTRY),
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

	public List<AutoSyncedAddon> getAddons() {
		return addons;
	}

	public ResourceKey<Level> getWorld() {
		return world;
	}
}
