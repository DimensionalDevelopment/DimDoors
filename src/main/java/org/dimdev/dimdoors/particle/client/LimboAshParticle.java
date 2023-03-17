package org.dimdev.dimdoors.particle.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LimboAshParticle extends BaseAshSmokeParticle {

	protected LimboAshParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteSet spriteProvider) {
		super(world, x, y, z, 0.1F, 0.1F, 0.01F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0F, 20, 0.0125F, false);
		this.rCol = 0.0431F;
		this.gCol = 0.0353F;
		this.bCol = 0.0352F;
		this.gravity = -gravity;
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			RandomSource random = clientWorld.random;
			double j = (double) random.nextFloat() * 0.4D * (double) random.nextFloat() * 0.1D;
			double k = (double) random.nextFloat() * 0.8D * (double) random.nextFloat() * 0.1D;// * 5.0D;
			double l = (double) random.nextFloat() * 0.4D * (double) random.nextFloat() * 0.1D;
			return new LimboAshParticle(clientWorld, d, e, f, j, k, l, 1.0F, this.spriteProvider);
		}
	}
}
