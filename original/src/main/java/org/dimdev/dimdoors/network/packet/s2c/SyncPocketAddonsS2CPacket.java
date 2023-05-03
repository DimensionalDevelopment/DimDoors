package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

public class SyncPocketAddonsS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = DimensionalDoors.id("sync_pocket_addons");

	private RegistryKey<World> world;
	private int gridSize;
	private int pocketId;
	private int pocketRange;
	private List<AutoSyncedAddon> addons;

	@Environment(EnvType.CLIENT)
	public SyncPocketAddonsS2CPacket() {
	}

	public SyncPocketAddonsS2CPacket(RegistryKey<World> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) {
		this.world = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(PacketByteBuf buf) throws IOException {
		this.world = RegistryKey.of(RegistryKeys.WORLD, buf.readIdentifier());
		this.gridSize = buf.readInt();
		this.pocketId = buf.readInt();
		this.pocketRange = buf.readInt();
		this.addons = AutoSyncedAddon.readAutoSyncedAddonList(buf);
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(world.getValue());
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
	public Identifier channelId() {
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

	public RegistryKey<World> getWorld() {
		return world;
	}
}
