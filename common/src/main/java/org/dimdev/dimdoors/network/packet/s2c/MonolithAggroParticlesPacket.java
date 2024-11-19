package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

public class MonolithAggroParticlesPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("monolith_aggro_particles");
	public static final CustomPacketPayload.Type<MonolithAggroParticlesPacket> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, MonolithAggroParticlesPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, MonolithAggroParticlesPacket::getAggro, MonolithAggroParticlesPacket::new);

	private int aggro;

	public MonolithAggroParticlesPacket(int aggro) {
		this.aggro = aggro;
	}

	public static void apply(MonolithAggroParticlesPacket packet, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onMonolithAggroParticles(packet);
	}

	public int getAggro() {
		return aggro;
	}

	@Override
	public Type<MonolithAggroParticlesPacket> type() {
		return TYPE;
	}
}
