package com.zixiken.dimdoors.shared.world.pocketdimension;

import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.pockets.EnumPocketType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class WorldProviderPersonalPocket extends WorldProviderPublicPocket {
    
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        setCloudRenderer(new CloudRenderBlank());
        return new Vec3d(1, 1, 1);
    }

    /*@Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            this.lightBrightnessTable[i] = (15);
        }
    }*/

    @Override
    public double getHorizon() {
        return world.getHeight() - 256;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3d(1, 1, 1);
    }

    @Override
    public int getActualHeight() {
        return -256;
    }
    
    @Override
    public EnumPocketType getPocketType() {
        return EnumPocketType.PRIVATE;
    }

    @Override
    public String getSaveFolder() {
        return "DIM" + getDimension() + "DimDoorsPersonal";
    }
}
