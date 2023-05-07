package org.dimdev.dimdoors.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

// each client has their own corresponding ServerPacketHandler, so feel free to add client specific data in here
public class ServerPacketHandler implements ServerPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();

	private final net.minecraft.network.protocol.game.ServerPacketListener networkHandler;
	private final Set<ResourceLocation> registeredChannels = new HashSet<>();
	private boolean initialized = false;

	private ResourceKey<Level> lastSyncedPocketWorld;
	private int lastSyncedPocketId = Integer.MIN_VALUE;
	private boolean pocketSyncDirty = true;

	public void init() {
		if (initialized) throw new RuntimeException("ServerPacketHandler has already been initialized.");
		initialized = true;
		registerReceiver(NetworkHandlerInitializedC2SPacket.ID, NetworkHandlerInitializedC2SPacket::new);
		registerReceiver(HitBlockWithItemC2SPacket.ID, HitBlockWithItemC2SPacket::new);
	}

	public static ServerPacketHandler get(ServerPlayer player) {
		return get(player.connection);
	}

	public static ServerPacketHandler get(ServerGamePacketListenerImpl networkHandler) {
		return ((ExtendedServerPlayNetworkHandler) networkHandler).getDimDoorsPacketHandler();
	}

	public static boolean sendPacket(ServerPlayer player, SimplePacket<?> packet) {
		try {
			NetworkManager.sendToPlayer(player, packet.channelId(), packet.write(PacketByteBufs.create()));
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	public boolean sendPacket(SimplePacket<?> packet) {
		return sendPacket(getPlayer(), packet);
	}

	public ServerPacketHandler(net.minecraft.network.protocol.game.ServerPacketListener networkHandler) {
		this.networkHandler = networkHandler;
	}

	private void registerReceiver(ResourceLocation channelName, Supplier<? extends SimplePacket<ServerPacketListener>> supplier) {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, channelName, new NetworkManager.NetworkReceiver() {
			@Override
			public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
				context.getPlayer()
			}
		});

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

	public void sync(ItemStack stack, Hand hand) {
		if (hand == Hand.OFF_HAND) {
			sendPacket(new PlayerInventorySlotUpdateS2CPacket(45, stack));
		} else {
			sendPacket(new PlayerInventorySlotUpdateS2CPacket(getPlayer().getInventory().selectedSlot, stack));
		}
	}

	@Override
	public void onAttackBlock(HitBlockWithItemC2SPacket packet) {
		getServer().execute(() -> {
			Item item = getPlayer().getStackInHand(packet.getHand()).getItem();
			if (item instanceof ExtendedItem) {
				((ExtendedItem) item).onAttackBlock(getPlayer().world, getPlayer(), packet.getHand(), packet.getPos(), packet.getDirection());
			}
		});
	}

	@Override
	public void onNetworkHandlerInitialized(NetworkHandlerInitializedC2SPacket packet) {
		syncPocketAddonsIfNeeded(getPlayer().world, getPlayer().getBlockPos());
	}
}