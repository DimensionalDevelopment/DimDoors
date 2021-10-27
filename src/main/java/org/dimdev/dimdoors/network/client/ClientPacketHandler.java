package org.dimdev.dimdoors.network.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.mixin.client.accessor.WorldRendererAccessor;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler implements ClientPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ClientPlayNetworkHandler networkHandler;
	private boolean initialized = false;

	private final Set<Identifier> registeredChannels = new HashSet<>();

	private RegistryKey<World> pocketWorld;
	private int gridSize = 1;
	private int pocketId = Integer.MIN_VALUE;
	private int pocketRange = 1;
	private List<AutoSyncedAddon> addons = new ArrayList<>();

	public void init() {
		if (initialized) throw new RuntimeException("ClientPacketHandler has already been initialized.");
		initialized = true;
		registerReceiver(PlayerInventorySlotUpdateS2CPacket.ID, PlayerInventorySlotUpdateS2CPacket::new);
		registerReceiver(SyncPocketAddonsS2CPacket.ID, SyncPocketAddonsS2CPacket::new);
		registerReceiver(MonolithAggroParticlesPacket.ID, MonolithAggroParticlesPacket::new);
		registerReceiver(MonolithTeleportParticlesPacket.ID, MonolithTeleportParticlesPacket::new);
		registerReceiver(RenderBreakBlockS2CPacket.ID, RenderBreakBlockS2CPacket::new);

		sendPacket(new NetworkHandlerInitializedC2SPacket());
	}

	public static boolean sendPacket(SimplePacket<?> packet) {
		try {
			ClientPlayNetworking.send(packet.channelId(), packet.write(PacketByteBufs.create()));
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	public ClientPacketHandler(ClientPlayNetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	private void registerReceiver(Identifier channelName, Supplier<? extends SimplePacket<ClientPacketListener>> supplier) {
		ClientPlayNetworking.registerReceiver(channelName, (client, handler, buf, responseSender) -> {
					try {
						supplier.get().read(buf).apply(this);
					} catch (IOException e) {
						LOGGER.error(e);
					}
				});
		registeredChannels.add(channelName);
	}

	private void unregisterReceiver(Identifier channelName) {
		ClientPlayNetworking.unregisterReceiver(channelName);
		registeredChannels.remove(channelName);
	}

	public void unregister() {
		new HashSet<>(registeredChannels).forEach(this::unregisterReceiver);
	}

	public RegistryKey<World> getPocketWorld() {
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
		MinecraftClient.getInstance().execute(() -> {
			if (MinecraftClient.getInstance().player != null) {
				MinecraftClient.getInstance().player.getInventory().setStack(packet.getSlot(), packet.getStack());
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
		MinecraftClient.getInstance().execute(() -> MonolithEntity.spawnParticles(packet.getAggro()));
	}

	@Override
	public void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet) {
		MinecraftClient client = MinecraftClient.getInstance();
		//noinspection ConstantConditions
		client.execute(() -> client.particleManager.addParticle(new MonolithParticle(client.world, client.player.getX(), client.player.getY(), client.player.getZ())));
	}

	@Override
	public void onRenderBreakBlock(RenderBreakBlockS2CPacket packet) {
		CustomBreakBlockHandler.customBreakBlock(packet.getPos(), packet.getStage(), ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getTicks());
	}
}
