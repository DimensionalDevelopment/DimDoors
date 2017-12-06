package com.zixiken.dimdoors.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClosingRiftFX extends Particle {

    private int baseTextureIndex = 160;
    private boolean trail;
    private boolean twinkle;
    private float fadeColourRed;
    private float fadeColourGreen;
    private float fadeColourBlue;
    private boolean hasFadeColour;

    public ClosingRiftFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        particleScale *= .55F;
        particleMaxAge = 30 + rand.nextInt(16);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
                               float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!twinkle || particleAge < particleMaxAge / 3  || (particleAge + particleMaxAge) / 3 % 2 == 0) {
            doRenderParticle(buffer, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }
    }

    public void doRenderParticle(BufferBuilder worldRenderer, float par2, float par3, float par4,
                                 float par5, float par6, float par7) {
        float var8 = super.particleTextureIndexX % 16 / 16.0F;
        float var9 = var8 + 0.0624375F;
        float var10 = particleTextureIndexX / 16 / 16.0F;
        float var11 = var10 + 0.0624375F;
        float var12 = 0.1F * particleScale;
        float var13 = (float) (prevPosX + (posX - prevPosX) * par2 - interpPosX);
        float var14 = (float) (prevPosY + (posY - prevPosY) * par2 - interpPosY);
        float var15 = (float) (prevPosZ + (posZ - prevPosZ) * par2 - interpPosZ);
        float var16 = 0.8F;

        worldRenderer.pos(var13 - par3 * var12 - par6 * var12, var14 - par4 * var12, var15 - par5 * var12 - par7 * var12)
                .tex(var9, var11)
                .color(particleRed * var16, particleGreen * var16, particleBlue * var16, (float) .7)
                .endVertex();
        worldRenderer.pos(var13 - par3 * var12 + par6 * var12, var14 + par4 * var12, var15 - par5 * var12 + par7 * var12)
                .tex(var9, var10)
                .color(particleRed * var16, particleGreen * var16, particleBlue * var16, (float) .7)
                .endVertex();
        worldRenderer.pos(var13 + par3 * var12 + par6 * var12, var14 + par4 * var12, var15 + par5 * var12 + par7 * var12)
                .tex(var8, var10)
                .color(particleRed * var16, particleGreen * var16, particleBlue * var16, (float) .7)
                .endVertex();
        worldRenderer.pos(var13 + par3 * var12 - par6 * var12, var14 - par4 * var12, var15 + par5 * var12 - par7 * var12)
                .tex(var8, var11)
                .color(particleRed * var16, particleGreen * var16, particleBlue * var16, (float) .7)
                .endVertex();
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        particleAge++;

        if (particleAge >= particleMaxAge) {
            setExpired();
        }
        if (particleAge > particleMaxAge / 2) {
            setAlphaF(1.0F - ((float) particleAge - (float) (particleMaxAge / 2)) / particleMaxAge);

            if (hasFadeColour) {
                particleRed += (fadeColourRed - particleRed) * 0.2F;
                particleGreen += (fadeColourGreen - particleGreen) * 0.2F;
                particleBlue += (fadeColourBlue - particleBlue) * 0.2F;
            }
        }

        setParticleTextureIndex(baseTextureIndex + 7 - particleAge * 8 / particleMaxAge);
        // this.motionY -= 0.004D;
        move(motionX, motionY, motionZ);
        motionX *= 0.9100000262260437D;
        motionY *= 0.9100000262260437D;
        motionZ *= 0.9100000262260437D;

        if (trail && particleAge < particleMaxAge / 2 && (particleAge + particleMaxAge) % 2 == 0) {
            ClosingRiftFX var1 = new ClosingRiftFX(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            var1.setRBGColorF(particleRed, particleGreen, particleBlue);
            var1.particleAge = var1.particleMaxAge / 2;

            if (hasFadeColour) {
                var1.hasFadeColour = true;
                var1.fadeColourRed = fadeColourRed;
                var1.fadeColourGreen = fadeColourGreen;
                var1.fadeColourBlue = fadeColourBlue;
            }

            var1.twinkle = twinkle;
        }
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        return 15728880;
    }
}
