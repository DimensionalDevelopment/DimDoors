package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.DimensionalDoors;

public record MonolithAggroParticlesPacket(int aggro) implements CustomPacketPayload {
	public static final Type<MonolithAggroParticlesPacket> TYPE = new Type<>(DimensionalDoors.id("monolith_aggro_particles"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MonolithAggroParticlesPacket> STREAM_CODEC = CustomPacketPayload.codec(MonolithAggroParticlesPacket::write, MonolithAggroParticlesPacket::new);

	private MonolithAggroParticlesPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(aggro);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
