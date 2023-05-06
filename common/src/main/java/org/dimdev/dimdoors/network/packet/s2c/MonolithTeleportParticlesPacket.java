package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

public class MonolithTeleportParticlesPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = DimensionalDoors.id("monolith_tp_particles");

	public MonolithTeleportParticlesPacket() {
	}

	@Override
	public SimplePacket<ClientPacketListener> read(FriendlyByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onMonolithTeleportParticles(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}
}
