package org.dimdev.dimdoors.network.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {
	private static final Logger LOGGER = LogManager.getLogger();


	private static ResourceKey<Level> pocketWorld;
	private static int gridSize = 1;
	private static int pocketId = Integer.MIN_VALUE;
	private static int pocketRange = 1;
	private static List<AutoSyncedAddon> addons = new ArrayList<>();

	public static ResourceKey<Level> getPocketWorld() {
		return pocketWorld;
	}

	public static int getGridSize() {
		return gridSize;
	}

	public static int getPocketId() {
		return pocketId;
	}

	public static int getPocketRange() {
		return pocketRange;
	}

	public static List<AutoSyncedAddon> getAddons() {
		return addons;
	}

	public static void update(SyncPocketAddonsS2CPacket packet) {
		ClientPacketHandler.pocketWorld =packet.world();
		ClientPacketHandler.gridSize =packet.gridSize();
		ClientPacketHandler.pocketId =packet.pocketId();
		ClientPacketHandler.pocketRange = packet.pocketRange();
		ClientPacketHandler.addons =packet.addons();
	}
}
