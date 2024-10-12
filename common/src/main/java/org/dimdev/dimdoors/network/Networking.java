package org.dimdev.dimdoors.network;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.util.*;


public class Networking {
	private static final ClientPacketLogic clientPacketLogic = EnvExecutor.getEnvSpecific(() -> ClientPacketLogicActive::new, () -> ClientPacketLogic::new);

	private static final Logger LOGGER = LogManager.getLogger();

	private static Map<UUID, PlayerNetworkState> STATES = new HashMap<>();

	public static void init() {
		PlayerEvent.PLAYER_JOIN.register(a -> STATES.put(a.getUUID(), new PlayerNetworkState()));
		PlayerEvent.PLAYER_QUIT.register(a -> STATES.put(a.getUUID(), new PlayerNetworkState()));

		registerC2SPacket(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC, HitBlockWithItemC2SPacket::handle);

//		NetworkManager.registerS2CPayloadType();DimensionalDoors.NETWORK.registerC2S( NetworkHandlerInitializedC2SPacket::write, NetworkHandlerInitializedC2SPacket::new, NetworkHandlerInitializedC2SPacket::apply);
//		NETWORK.register(HitBlockWithItemC2SPacket.class, HitBlockWithItemC2SPacket::write, HitBlockWithItemC2SPacket::new, HitBlockWithItemC2SPacket::apply);

		registerS2CPacket(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC, Networking::onPlayerInventorySlotUpdate);
		registerS2CPacket(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC, Networking::onSyncPocketAddons);
		registerS2CPacket(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC, Networking::onMonolithAggroParticles);
		registerS2CPacket(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC, Networking::onMonolithTeleportParticles);
		registerS2CPacket(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC, Networking::onRenderBreakBlock);
	}

	private static <T extends CustomPacketPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, NetworkManager.NetworkReceiver<T> consumer) {
//		NetworkManager.registerS2CPayloadType(type, streamCodec);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, type, streamCodec, consumer);
	}

	private static <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, NetworkManager.NetworkReceiver<T> consumer) {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, type, streamCodec, consumer);
	}

	public static Networking get(ServerGamePacketListenerImpl networkHandler) {
		return ((ExtendedServerPlayNetworkHandler) networkHandler).getDimDoorsPacketHandler();
	}

	public static <T extends CustomPacketPayload> boolean sendPacket(ServerPlayer player, T packet) {
		try {
			player.connection.send(NetworkManager.toPacket(NetworkManager.Side.S2C, packet, player.level().registryAccess()));
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	public static <T extends CustomPacketPayload> boolean sendPacket(T packet) {
		try {
			clientPacketLogic.sendPacket(packet);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	public static void sync(ServerPlayer player, ItemStack stack, InteractionHand hand) {
		if (hand == InteractionHand.OFF_HAND) {
			sendPacket(player, new PlayerInventorySlotUpdateS2CPacket(45, stack));
		} else {
			sendPacket(player, new PlayerInventorySlotUpdateS2CPacket(player.getInventory().selected, stack));
		}
	}

	public static Networking.PlayerNetworkState getNetworkState(Player serverPlayer) {
		return STATES.get(serverPlayer.getUUID());
	}

//	public static void onNetworkHandlerInitialized(NetworkHandlerInitializedC2SPacket packet, NetworkManager.PacketContext context) {
//		getNetworkState(context.getPlayer()).syncPocketAddonsIfNeeded(context.getPlayer());
//	}

	public static class PlayerNetworkState {
		private ResourceKey<Level> lastSyncedPocketWorld;
		private int lastSyncedPocketId = Integer.MIN_VALUE;
		private boolean pocketSyncDirty = true;

		// TODO: attach this to some event to detect other kinds teleportation
		public void syncPocketAddonsIfNeeded(Player player, Level world, BlockPos pos) {
			if (!ModDimensions.isPocketDimension(world)) return;
			PocketDirectory directory = DimensionalRegistry.getPocketDirectory(world.dimension());
			Pocket pocket = directory.getPocketAt(pos);
			if (pocket == null) return;

			if ((pocketSyncDirty || pocket.getId() != lastSyncedPocketId || !world.dimension().location().equals(lastSyncedPocketWorld.location()))) {
				pocketSyncDirty = false;
				lastSyncedPocketId = pocket.getId();
				lastSyncedPocketWorld = world.dimension();
				sendPacket((ServerPlayer) player, new SyncPocketAddonsS2CPacket(world.dimension(), directory.getGridSize(), pocket.getId(), pocket.getRange(), pocket.getAddonsInstanceOf(AutoSyncedAddon.class)));
			}
		}

		public void markPocketSyncDirty(int id) {
			if (lastSyncedPocketId == id) pocketSyncDirty = true;
		}

		public void syncPocketAddonsIfNeeded(Player player) {
			syncPocketAddonsIfNeeded(player, player.level(), player.blockPosition());
		}
	}

	public static void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet, NetworkManager.PacketContext context) {
		clientPacketLogic.onPlayerInventorySlotUpdate(packet, context);
	}

	public static void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet, NetworkManager.PacketContext context) {
		clientPacketLogic.onSyncPocketAddons(packet, context);
	}

	public static void onMonolithAggroParticles(MonolithAggroParticlesPacket packet, NetworkManager.PacketContext context) {
		clientPacketLogic.onMonolithAggroParticles(packet, context);
	}

	public static void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet, NetworkManager.PacketContext context) {
		clientPacketLogic.onMonolithTeleportParticles(packet, context);
	}

	public static void onRenderBreakBlock(RenderBreakBlockS2CPacket packet, NetworkManager.PacketContext context) {
		clientPacketLogic.onRenderBreakBlock(packet, context);
	}
}
