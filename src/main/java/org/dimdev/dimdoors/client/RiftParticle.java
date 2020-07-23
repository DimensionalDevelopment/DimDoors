package org.dimdev.dimdoors.client;

import java.util.Random;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

public class RiftParticle extends AnimatedParticle {
    public RiftParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float color, float scale, int averageAge, int ageSpread) {
        super(world, x, y, z, new SpriteProvider() { // TODO: 160, 8
            @Override
            public Sprite getSprite(int i, int j) {
                return null;
            }

            @Override
            public Sprite getSprite(Random random) {
                return null;
            }
        }, 0);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        scale *= scale;
        maxAge = averageAge - ageSpread / 2 + random.nextInt(ageSpread);

        setColor(color, color, color);
    }

    @Override
    public void tick() {
        setColorAlpha(1 - (float) age / maxAge);
    }
}
