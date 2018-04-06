package org.dimdev.dimdoors.client;

import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.world.ModDimensions;

// This has exactly the same appearence as the 1.6.4 mod.
@SideOnly(Side.CLIENT)
public class ParticleRiftEffect extends ParticleSimpleAnimated { // TODO: colors, density

    private float colorMultiplier;

    public ParticleRiftEffect(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float nonPocketColorMultiplier, float pocketColorMultiplier, float scale, int averageAge, int ageSpread) {
        super(world, x, y, z, 160, 8, 0);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        particleScale *= scale;
        particleMaxAge = averageAge - ageSpread / 2 + rand.nextInt(ageSpread);
        colorMultiplier = ModDimensions.isDimDoorsPocketDimension(world) ? pocketColorMultiplier : nonPocketColorMultiplier;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float oldRed = particleRed;
        float oldGreen = particleGreen;
        float oldBlue = particleBlue;
        float oldAlpha = particleAlpha;
        setRBGColorF(colorMultiplier * particleRed, colorMultiplier * particleGreen, colorMultiplier * particleBlue);
        setAlphaF(1 - (float) particleAge / particleMaxAge);
        super.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        setRBGColorF(oldRed, oldGreen, oldBlue);
        setAlphaF(oldAlpha);
    }

    public static class Rift extends ParticleRiftEffect {
        public Rift(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            super(world, x, y, z, motionX, motionY, motionZ, 0.0f, 0.7f, 0.55f, 2000, 2000);
        }
    }

    public static class ClosingRiftEffect extends ParticleRiftEffect {
        public ClosingRiftEffect(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            super(world, x, y, z, motionX, motionY, motionZ, 0.8f, 0.4f, 0.55f, 38, 16);
        }
    }
}
