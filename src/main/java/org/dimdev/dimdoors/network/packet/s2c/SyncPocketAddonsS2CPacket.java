package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

public class SyncPocketAddonsS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.resource("sync_pocket_addons");

	private ResourceKey<Level> world;
	private int gridSize;
	private int pocketId;
	private int pocketRange;
	private List<AutoSyncedAddon> addons;

	@Environment(Dist.CLIENT)
	public SyncPocketAddonsS2CPacket() {
	}

	public SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) {
		this.world = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(FriendlyByteBuf buf) throws IOException {
		this.world = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
		this.gridSize = buf.readInt();
		this.pocketId = buf.readInt();
		this.pocketRange = buf.readInt();
		this.addons = AutoSyncedAddon.readAutoSyncedAddonList(buf);
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeResourceLocation(world.location());
		buf.writeInt(gridSize);
		buf.writeInt(pocketId);
		buf.writeInt(pocketRange);
		AutoSyncedAddon.writeAutoSyncedAddonList(buf, addons);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onSyncPocketAddons(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
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
