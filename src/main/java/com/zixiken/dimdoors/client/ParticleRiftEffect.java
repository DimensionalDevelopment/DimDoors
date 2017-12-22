package com.zixiken.dimdoors.client;

import ddutils.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

// This has exactly the same appearence as the 1.6.4 mod.
public class ParticleRiftEffect extends ParticleSimpleAnimated { // TODO: colors, density

    private float colorMultiplier;

    public ParticleRiftEffect(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float nonPocketColorMultiplier, float pocketColorMultiplier, float scale, int size, int spread) {
        super(world, x, y, z, 160, 8, 0);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        particleScale *= scale;
        particleMaxAge = size - spread / 2 + rand.nextInt(spread);
        colorMultiplier = DimDoorDimensions.isPocketDimension(WorldUtils.getDim(world)) ? pocketColorMultiplier : nonPocketColorMultiplier;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (particleAge < particleMaxAge / 3 || (particleAge + particleMaxAge) / 3 % 2 == 0) {
            float oldRed = particleRed;
            float oldGreen = particleGreen;
            float oldBlue = particleBlue;
            float oldAlpha = particleAlpha;
            setRBGColorF(colorMultiplier * particleRed, colorMultiplier * particleGreen, colorMultiplier * particleBlue);
            setAlphaF(0.7f);
            super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            setRBGColorF(oldRed, oldGreen, oldBlue);
            setAlphaF(oldAlpha);
        }
    }

    public static class Rift extends ParticleRiftEffect {
        public Rift(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            super(world, x, y, z, motionX, motionY, motionZ, 0.0f, 0.7f, 0.55f, 38, 16);
        }
    }

    public static class ClosingRiftEffect extends ParticleRiftEffect {
        public ClosingRiftEffect(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            super(world, x, y, z, motionX, motionY, motionZ, 0.8f, 0.4f, 0.55f, 38, 16);
        }
    }

    public static class GogglesRiftEffect extends ParticleRiftEffect {
        public GogglesRiftEffect(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            super(world, x, y, z, motionX, motionY, motionZ, 0.0f, 0.7f, 0.55f, 38, 16);
        }
    }
}
