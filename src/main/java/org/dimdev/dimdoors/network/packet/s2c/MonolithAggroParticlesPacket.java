package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;
import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

public class MonolithAggroParticlesPacket implements SimplePacket<ClientPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.resource("monolith_aggro_particles");

	private int aggro;

	@OnlyIn(Dist.CLIENT)
	public MonolithAggroParticlesPacket() {
	}

	public MonolithAggroParticlesPacket(int aggro) {
		this.aggro = aggro;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(FriendlyByteBuf buf) throws IOException {
		return new MonolithAggroParticlesPacket(buf.readVarInt());
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeVarInt(aggro);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onMonolithAggroParticles(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}

	public int getAggro() {
		return aggro;
	}
}
