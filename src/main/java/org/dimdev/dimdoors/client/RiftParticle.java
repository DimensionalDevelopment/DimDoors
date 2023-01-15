package org.dimdev.dimdoors.client;

import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RiftParticle extends ParticleSimpleAnimated {

    public RiftParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ,
                        float color, float scale, int averageAge, int ageSpread) {
        super(world, x, y, z, 160, 8, 0);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        particleScale *= scale;
        particleMaxAge = averageAge - ageSpread / 2 + rand.nextInt(ageSpread);
        setRBGColorF(color, color, color);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ,
                               float rotationYZ, float rotationXY, float rotationXZ) {
        setAlphaF(1 - (float) particleAge / particleMaxAge);
        super.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
