package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.Util;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import java.io.IOException;

public class MonolithTeleportParticlesPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = Util.id("monolith_tp_particles");

	public MonolithTeleportParticlesPacket() {
	}

	@Override
	public SimplePacket<ClientPacketListener> read(PacketByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onMonolithTeleportParticles(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}
}
