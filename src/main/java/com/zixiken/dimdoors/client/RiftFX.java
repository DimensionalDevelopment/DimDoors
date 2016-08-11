package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RiftFX extends EntityFX {
    private int baseTextureIndex = 160;
    private boolean trail;
    private boolean twinkle;
    private final EffectRenderer effectRenderer;
    private float fadeColourRed;
    private float fadeColourGreen;
    private float fadeColourBlue;
    private boolean hasFadeColour;

    public RiftFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, EffectRenderer effectRenderer) {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.effectRenderer = effectRenderer;
        this.particleScale *= 0.75F;
        this.particleMaxAge = 40 + this.rand.nextInt(26);
        this.noClip = true;
    }

    /**
     * returns the bounding box for this entity
     */
    @Override
	public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
	public boolean canBePushed()
    {
        return false;
    }

    @Override
	public void renderParticle(WorldRenderer worldRenderer, Entity entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
            this.doRenderParticle(worldRenderer, par2, par3, par4, par5, par6, par7);
        }
    }
    
    public void doRenderParticle(WorldRenderer worldRenderer, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float f6 = this.particleTextureIndexX / 16.0F;
        float f7 = f6 + 0.0624375F;
        float f8 = this.particleTextureIndexY / 16.0F;
        float f9 = f8 + 0.0624375F;
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);
        float f14 = 0F;
        
        if (PocketManager.createDimensionData(worldObj).isPocketDimension()) {
    		f14 = 0.7F;
    	}

        worldRenderer.pos(f11 - par3 * f10 - par6 * f10, f12 - par4 * f10, f13 - par5 * f10 - par7 * f10).tex(f7, f9).color(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, (float) .7).endVertex();
        worldRenderer.pos(f11 - par3 * f10 + par6 * f10, f12 + par4 * f10, f13 - par5 * f10 + par7 * f10).tex(f7, f8).color(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, (float) .7).endVertex();
        worldRenderer.pos(f11 + par3 * f10 + par6 * f10, f12 + par4 * f10, f13 + par5 * f10 + par7 * f10).tex(f6, f8).color(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, (float) .7).endVertex();
        worldRenderer.pos(f11 + par3 * f10 - par6 * f10, f12 - par4 * f10, f13 + par5 * f10 - par7 * f10).tex(f6, f9).color(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, (float) .7).endVertex();
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        if (this.particleAge > this.particleMaxAge / 2) {
            this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / this.particleMaxAge);

            if (this.hasFadeColour) {
                this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
                this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
                this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
            }
        }

        this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
       // this.motionY -= 0.004D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9100000262260437D;
        this.motionY *= 0.9100000262260437D;
        this.motionZ *= 0.9100000262260437D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
            RiftFX rift = new RiftFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.effectRenderer);
            rift.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
            rift.particleAge = rift.particleMaxAge / 2;

            if (this.hasFadeColour) {
                rift.hasFadeColour = true;
                rift.fadeColourRed = this.fadeColourRed;
                rift.fadeColourGreen = this.fadeColourGreen;
                rift.fadeColourBlue = this.fadeColourBlue;
            }

            rift.twinkle = this.twinkle;
            this.effectRenderer.addEffect(rift);
        }
    }

    @Override
	public int getBrightnessForRender(float par1) {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
	public float getBrightness(float par1) {
        return 1.0F;
    }
}
