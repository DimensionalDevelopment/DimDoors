package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public class MonolithTeleportParticlesPacket implements CustomPacketPayload {
	public static final Type<MonolithTeleportParticlesPacket> TYPE = new Type<>(DimensionalDoors.id("monolith_tp_particles"));
	public static final MonolithTeleportParticlesPacket INSTANCE = new MonolithTeleportParticlesPacket();
	public static final StreamCodec<RegistryFriendlyByteBuf, MonolithTeleportParticlesPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
