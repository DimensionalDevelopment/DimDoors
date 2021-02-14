package org.dimdev.dimdoors.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.network.s2c.PlayerInventorySlotUpdateS2CPacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ClientPlayNetworkHandler networkHandler;
	private final MinecraftClient client;

	private final Set<Identifier> registeredChannels = new HashSet<>();

	private void registerModReceivers() {
		registerReceiver(PlayerInventorySlotUpdateS2CPacket.ID, PlayerInventorySlotUpdateS2CPacket::new);
	}

	public ClientPacketHandler(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
		this.networkHandler = networkHandler;
		this.client = client;
		registerModReceivers();
	}

	private void registerReceiver(Identifier channelName, Supplier<? extends SimplePacket<ClientPacketHandler>> supplier) {
		ClientPlayNetworking.registerReceiver(channelName,
				(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
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

	public void onPlayerInventorySlotUpdate(int slot, ItemStack stack) {
		if (client.player != null) {
			this.client.player.getInventory().setStack(slot, stack);
		}

	}
}
