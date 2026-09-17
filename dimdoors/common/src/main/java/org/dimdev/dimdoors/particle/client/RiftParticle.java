package org.dimdev.dimdoors.particle.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class RiftParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public RiftParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float color, int ageSpread, SpriteSet spriteProvider) {
        super(world, x, y, z, 160, 8, 0);
        this.gravity = 0f;
        this.friction = 0.91F;
        this.sprites = spriteProvider;
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;

        this.quadSize *= 0.55f;
        this.lifetime = ageSpread - ageSpread / 2 + this.random.nextInt(ageSpread);

        this.setColor(color, color, color);
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        this.setSpriteFromAge(this.sprites);
        setAlpha(1 - (float) age / lifetime);
        super.render(buffer, renderInfo, partialTicks);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTick) {
        return 15728880;
    }

    public static class Factory implements ParticleProvider<RiftParticleOptions> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(RiftParticleOptions riftParticleOptions, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RiftParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, riftParticleOptions.color(), riftParticleOptions.averageAge(), this.spriteProvider);
        }
    }
}
