package org.dimdev.dimdoors.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimdoors.network.ServerPacketHandler;

import java.util.function.Supplier;

public record NetworkHandlerInitializedC2SPacket() {
	public NetworkHandlerInitializedC2SPacket(FriendlyByteBuf buf) {
		this();
	}
	public void write(FriendlyByteBuf buf) {
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ServerPacketHandler.get((ServerPlayer) context.get().getPlayer()).onNetworkHandlerInitialized(this);
	}
}
