package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;

import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MonolithAggroParticlesPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors", "monolith_aggro_particles");

	private int aggro;

	@Environment(EnvType.CLIENT)
	public MonolithAggroParticlesPacket() {
	}

	public MonolithAggroParticlesPacket(int aggro) {
		this.aggro = aggro;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(PacketByteBuf buf) throws IOException {
		return new MonolithAggroParticlesPacket(buf.readVarInt());
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(aggro);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onMonolithAggroParticles(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}

	public int getAggro() {
		return aggro;
	}
}
