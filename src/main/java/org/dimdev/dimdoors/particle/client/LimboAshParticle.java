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
		this.colorRed = 0.0431F;
		this.colorGreen = 0.0353F;
		this.colorBlue = 0.0352F;
		this.gravityStrength = -gravityStrength;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Random random = clientWorld.random;
			double j = (double)random.nextFloat() * 0.4D * (double)random.nextFloat() * 0.1D;
			double k = (double)random.nextFloat() * 0.8D * (double)random.nextFloat() * 0.1D;// * 5.0D;
			double l = (double)random.nextFloat() * 0.4D * (double)random.nextFloat() * 0.1D;
			return new LimboAshParticle(clientWorld, d, e, f, j, k, l, 1.0F, this.spriteProvider);
		}
	}
}
