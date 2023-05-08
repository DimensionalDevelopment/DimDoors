package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

public class MonolithTeleportParticlesPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("monolith_tp_particles");

	public MonolithTeleportParticlesPacket() {
	}

	public MonolithTeleportParticlesPacket(FriendlyByteBuf buf) {
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		return buf;
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ClientPacketHandler.getHandler().onMonolithTeleportParticles(this);
	}
}
