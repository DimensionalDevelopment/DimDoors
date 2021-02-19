package org.dimdev.dimdoors.network.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.SimplePacket;

import java.io.IOException;

public class NetworkHandlerInitializedC2SPacket implements SimplePacket<ServerPacketHandler> {
	public static final Identifier ID = new Identifier("dimdoors", "network_handler_initialized");

	@Override
	public SimplePacket<ServerPacketHandler> read(PacketByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public void apply(ServerPacketHandler listener) {
		listener.onNetworkHandlerInitialized();
	}

	@Override
	public Identifier channelId() {
		return ID;
	}
}
