package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

public class MonolithTeleportParticlesPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("monolith_tp_particles");
	public static final CustomPacketPayload.Type<MonolithTeleportParticlesPacket> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final MonolithTeleportParticlesPacket INSTANCE = new MonolithTeleportParticlesPacket();
	public static final StreamCodec<RegistryFriendlyByteBuf, MonolithTeleportParticlesPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	public static void apply(MonolithTeleportParticlesPacket packet, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onMonolithTeleportParticles(packet);
	}

	@Override
	public Type<MonolithTeleportParticlesPacket> type() {
		return TYPE;
	}
}
