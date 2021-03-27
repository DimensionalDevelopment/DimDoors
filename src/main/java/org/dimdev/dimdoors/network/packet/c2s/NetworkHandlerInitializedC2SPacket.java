package org.dimdev.dimdoors.network.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.network.ServerPacketListener;
import org.dimdev.dimdoors.network.SimplePacket;

import java.io.IOException;

public class NetworkHandlerInitializedC2SPacket implements SimplePacket<ServerPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors", "network_handler_initialized");

	@Override
	public SimplePacket<ServerPacketListener> read(PacketByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public void apply(ServerPacketListener listener) {
		listener.onNetworkHandlerInitialized(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}
}
