package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class MonolithAggroParticlesPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("monolith_aggro_particles");
	@Environment(EnvType.CLIENT)
	private static final RandomSource clientRandom = RandomSource.create();

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

	@Environment(EnvType.CLIENT)
	public static void spawnParticles(int aggro) {
		Player player = Minecraft.getInstance().player;
		if (aggro < 120) {
			return;
		}
		int count = 10 * aggro / MAX_AGGRO;
		for (int i = 1; i < count; ++i) {
			//noinspection ConstantConditions
			player.level.addParticle(ParticleTypes.PORTAL, player.getX() + (clientRandom.nextDouble() - 0.5D) * 3.0,
					player.getY() + clientRandom.nextDouble() * player.getBbHeight() - 0.75D,
					player.getZ() + (clientRandom.nextDouble() - 0.5D) * player.getBbWidth(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D, -clientRandom.nextDouble(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D);
		}
	}
}
