package org.dimdev.dimdoors.network.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.mixin.client.accessor.WorldRendererAccessor;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler implements ClientPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();

	private final net.minecraft.client.multiplayer.ClientPacketListener networkHandler;

	private ResourceKey<Level> pocketWorld;
	private int gridSize = 1;
	private int pocketId = Integer.MIN_VALUE;
	private int pocketRange = 1;
	private List<AutoSyncedAddon> addons = new ArrayList<>();

	public static void init() {
		DimensionalDoors.NETWORK.register(PlayerInventorySlotUpdateS2CPacket.class, PlayerInventorySlotUpdateS2CPacket::write, PlayerInventorySlotUpdateS2CPacket::new, PlayerInventorySlotUpdateS2CPacket::apply);
		DimensionalDoors.NETWORK.register(SyncPocketAddonsS2CPacket.class, SyncPocketAddonsS2CPacket::write, SyncPocketAddonsS2CPacket::new, SyncPocketAddonsS2CPacket::apply);
		DimensionalDoors.NETWORK.register(MonolithAggroParticlesPacket.class, MonolithAggroParticlesPacket::write, MonolithAggroParticlesPacket::new, MonolithAggroParticlesPacket::apply);
		DimensionalDoors.NETWORK.register(MonolithTeleportParticlesPacket.class, MonolithTeleportParticlesPacket::write, MonolithTeleportParticlesPacket::new, MonolithTeleportParticlesPacket::apply);
		DimensionalDoors.NETWORK.register(RenderBreakBlockS2CPacket.class, RenderBreakBlockS2CPacket::write, RenderBreakBlockS2CPacket::new, RenderBreakBlockS2CPacket::apply);
	}

	public static <T> boolean sendPacket(T packet) {
		try {
			DimensionalDoors.NETWORK.sendToServer(packet);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	public ClientPacketHandler(net.minecraft.client.multiplayer.ClientPacketListener networkHandler) {
		this.networkHandler = networkHandler;
	}

	public static ClientPacketHandler getHandler() {
		return ((ExtendedClientPlayNetworkHandler) Minecraft.getInstance().getConnection()).getDimDoorsPacketHandler();
	}

	public ResourceKey<Level> getPocketWorld() {
		return pocketWorld;
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

	@Override
	public void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet) {
		Minecraft.getInstance().execute(() -> {
			if (Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.getInventory().setItem(packet.getSlot(), packet.getStack());
			}
		});
	}

	@Override
	public void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet) {
		this.pocketWorld = packet.getWorld();
		this.gridSize = packet.getGridSize();
		this.pocketId = packet.getPocketId();
		this.pocketRange = packet.getPocketRange();
		this.addons = packet.getAddons();
	}

	@Override
	public void onMonolithAggroParticles(MonolithAggroParticlesPacket packet) {
		Minecraft.getInstance().execute(() -> MonolithEntity.spawnParticles(packet.getAggro()));
	}

	@Override
	public void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet) {
		Minecraft client = Minecraft.getInstance();
		//noinspection ConstantConditions
		client.execute(() -> client.particleEngine.add(new MonolithParticle(client.level, client.player.getX(), client.player.getY(), client.player.getZ())));
	}

	@Override
	public void onRenderBreakBlock(RenderBreakBlockS2CPacket packet) {
		CustomBreakBlockHandler.customBreakBlock(packet.getPos(), packet.getStage(), ((WorldRendererAccessor) Minecraft.getInstance().levelRenderer).getTicks());
	}
}
