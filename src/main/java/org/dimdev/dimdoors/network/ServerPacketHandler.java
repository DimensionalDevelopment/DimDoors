package org.dimdev.dimdoors.network;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.item.ModItem;
import org.dimdev.dimdoors.network.c2s.HitBlockWithItemC2SPacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

// each client has their own corresponding ServerPacketHandler, so feel free to add client specific data in here
public class ServerPacketHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ServerPlayNetworkHandler networkHandler;
	private final MinecraftServer server;
	private final Set<Identifier> registeredChannels = new HashSet<>();

	private void registerModReceivers() {
		registerReceiver(HitBlockWithItemC2SPacket.ID, HitBlockWithItemC2SPacket::new);
	}


	public ServerPacketHandler(ServerPlayNetworkHandler networkHandler, MinecraftServer server) {
		this.networkHandler = networkHandler;
		this.server = server;
		registerModReceivers();
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

	public ServerPlayerEntity getPlayer() {
		return networkHandler.player;
	}

	public void onAttackBlock(Hand hand, BlockPos pos, Direction direction) {
		server.execute(() -> {
			Item item = getPlayer().getStackInHand(hand).getItem();
			if (item instanceof ModItem) {
				((ModItem) item).onAttackBlock(getPlayer().world, getPlayer(), hand, pos, direction);
			}
		});
	}
}
