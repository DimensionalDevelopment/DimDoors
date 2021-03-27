package org.dimdev.dimdoors.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.item.ModItem;
import org.dimdev.dimdoors.network.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

// each client has their own corresponding ServerPacketHandler, so feel free to add client specific data in here
public class ServerPacketHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ServerPlayNetworkHandler networkHandler;
	private final Set<Identifier> registeredChannels = new HashSet<>();
	private boolean initialized = false;

	private RegistryKey<World> lastSyncedPocketWorld;
	private int lastSyncedPocketId = Integer.MIN_VALUE;
	private boolean pocketSyncDirty = true;

	public void init() {
		if (initialized) throw new RuntimeException("ServerPacketHandler has already been initialized.");
		initialized = true;
		registerReceiver(NetworkHandlerInitializedC2SPacket.ID, NetworkHandlerInitializedC2SPacket::new);
		registerReceiver(HitBlockWithItemC2SPacket.ID, HitBlockWithItemC2SPacket::new);
	}

	public static boolean sendPacket(ServerPlayerEntity player, SimplePacket<?> packet) {
		try {
			ServerPlayNetworking.send(player, packet.channelId(), packet.write(PacketByteBufs.create()));
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	public ServerPacketHandler(ServerPlayNetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	private void registerReceiver(Identifier channelName, Supplier<? extends SimplePacket<ServerPacketHandler>> supplier) {
		ServerPlayNetworking.registerReceiver(networkHandler, channelName,
				(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
					try {
						supplier.get().read(buf).apply(this);
					} catch (IOException e) {
						LOGGER.error(e);
					}
				});
		registeredChannels.add(channelName);
	}

	private void unregisterReceiver(Identifier channelName) {
		ServerPlayNetworking.unregisterReceiver(networkHandler, channelName);
		registeredChannels.remove(channelName);
	}

	public void unregister() {
		new HashSet<>(registeredChannels).forEach(this::unregisterReceiver);
	}

	public MinecraftServer getServer() {
		return ((ExtendedServerPlayNetworkHandler) networkHandler).dimdoorsGetServer();
	}

	public ServerPlayerEntity getPlayer() {
		return networkHandler.player;
	}

	public void onAttackBlock(Hand hand, BlockPos pos, Direction direction) {
		getServer().execute(() -> {
			Item item = getPlayer().getStackInHand(hand).getItem();
			if (item instanceof ModItem) {
				((ModItem) item).onAttackBlock(getPlayer().world, getPlayer(), hand, pos, direction);
			}
		});
	}

	public void onNetworkHandlerInitialized() {
		syncPocketAddonsIfNeeded(getPlayer().world, getPlayer().getBlockPos());
	}

	// TODO: attach this to some event to detect other kinds teleportation
	public void syncPocketAddonsIfNeeded(World world, BlockPos pos) {
		if (!ModDimensions.isPocketDimension(world)) return;
		PocketDirectory directory = DimensionalRegistry.getPocketDirectory(world.getRegistryKey());
		Pocket pocket = directory.getPocketAt(pos);
		if (pocket == null) return;
		if ((pocketSyncDirty || pocket.getId() != lastSyncedPocketId || !world.getRegistryKey().getValue().equals(lastSyncedPocketWorld.getValue()))) {
			pocketSyncDirty = false;
			lastSyncedPocketId = pocket.getId();
			lastSyncedPocketWorld = world.getRegistryKey();
			sendPacket(getPlayer(), new SyncPocketAddonsS2CPacket(world.getRegistryKey(), directory.getGridSize(), pocket.getId(), pocket.getRange(), pocket.getAddonsInstanceOf(AutoSyncedAddon.class)));
		}
	}

	public void markPocketSyncDirty(int id) {
		if (lastSyncedPocketId == id) pocketSyncDirty = true;
	}
}
