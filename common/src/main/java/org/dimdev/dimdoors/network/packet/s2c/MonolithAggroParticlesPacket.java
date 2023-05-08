package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

public class MonolithAggroParticlesPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("monolith_aggro_particles");

	private int aggro;

	@Environment(EnvType.CLIENT)
	public MonolithAggroParticlesPacket() {
	}

	public MonolithAggroParticlesPacket(int aggro) {
		this.aggro = aggro;
	}

	public MonolithAggroParticlesPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeVarInt(aggro);
		return buf;
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ClientPacketHandler.getHandler().onMonolithAggroParticles(this);
	}

	public int getAggro() {
		return aggro;
	}
}
