package com.zixiken.dimdoors.client;

import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GoggleRiftFX extends ParticleCloud {

    public GoggleRiftFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        super(par1World, par2, par4, par6, par12, par12, par12);
        particleMaxAge = 40 + rand.nextInt(26);
    }
}
