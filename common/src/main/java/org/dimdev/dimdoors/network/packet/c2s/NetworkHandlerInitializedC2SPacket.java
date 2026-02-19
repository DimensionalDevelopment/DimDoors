package org.dimdev.dimdoors.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.ServerPacketHandler;

public record NetworkHandlerInitializedC2SPacket() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, NetworkHandlerInitializedC2SPacket> STREAM_CODEC = StreamCodec.unit(new NetworkHandlerInitializedC2SPacket());
	public static final Type<NetworkHandlerInitializedC2SPacket> TYPE = new Type<NetworkHandlerInitializedC2SPacket>(DimensionalDoors.id("network_handler_initialized"));

	@Override
	public Type<NetworkHandlerInitializedC2SPacket> type() {
		return TYPE;
	}
}
