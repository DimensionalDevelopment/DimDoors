package org.dimdev.dimdoors.particle.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class LimboAshParticle extends AscendingParticle {

	protected LimboAshParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.1F, 0.1F, 0.01F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0F, 20, 0.0125F, false);
		this.red = 0.0431F;
		this.green = 0.0353F;
		this.blue = 0.0352F;
		this.gravityStrength = -gravityStrength;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Random random = clientWorld.random;
			double j = (double) random.nextFloat() * 0.4D * (double) random.nextFloat() * 0.1D;
			double k = (double) random.nextFloat() * 0.8D * (double) random.nextFloat() * 0.1D;// * 5.0D;
			double l = (double) random.nextFloat() * 0.4D * (double) random.nextFloat() * 0.1D;
			return new LimboAshParticle(clientWorld, d, e, f, j, k, l, 1.0F, this.spriteProvider);
		}
	}
}
