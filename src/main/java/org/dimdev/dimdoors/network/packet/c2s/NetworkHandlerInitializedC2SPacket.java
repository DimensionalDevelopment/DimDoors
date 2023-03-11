package org.dimdev.dimdoors.network.packet.c2s;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.ServerPacketListener;
import org.dimdev.dimdoors.network.SimplePacket;

public class NetworkHandlerInitializedC2SPacket implements SimplePacket<ServerPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.id("network_handler_initialized");

	@Override
	public SimplePacket<ServerPacketListener> read(FriendlyByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public void apply(ServerPacketListener listener) {
		listener.onNetworkHandlerInitialized(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}
}
