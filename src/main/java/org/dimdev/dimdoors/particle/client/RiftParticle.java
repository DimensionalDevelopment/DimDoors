package org.dimdev.dimdoors.particle.client;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import org.dimdev.dimdoors.particle.RiftParticleType;

public class RiftParticle extends AnimatedParticle {
    public RiftParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float color, int ageSpread, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.scale *= 0.55f;
        this.maxAge = ageSpread - ageSpread / 2 + this.random.nextInt(2000);

        this.setColor(color, color, color);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return super.getType();
    }

    public static class Factory implements ParticleFactory<RiftParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(RiftParticleEffect riftParticleEffect, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RiftParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, riftParticleEffect.getColor(), riftParticleEffect.getAverageAge(), this.spriteProvider);
        }
    }
}
