package org.dimdev.dimdoors.particle.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;

public class RiftParticle extends SimpleAnimatedParticle {
    public RiftParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float color, int ageSpread, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;

        this.quadSize *= 0.55f;
        // FIXME: Math.min is just a band-aid fix to prevent a crash, need to ensure (age <= lifetime)
        this.age = Math.min(ageSpread - ageSpread / 2 + this.random.nextInt(2000), this.lifetime);

        this.setColor(color, color, color);
        this.setSpriteFromAge(spriteProvider);
    }

    public static class Factory implements ParticleProvider<RiftParticleEffect> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(RiftParticleEffect riftParticleEffect, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RiftParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, riftParticleEffect.getColor(), riftParticleEffect.getAverageAge(), this.spriteProvider);
        }
    }
}
