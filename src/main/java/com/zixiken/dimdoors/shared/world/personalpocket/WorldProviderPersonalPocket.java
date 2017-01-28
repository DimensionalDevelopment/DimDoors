package com.zixiken.dimdoors.shared.world.personalpocket;

import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import com.zixiken.dimdoors.shared.world.pocket.WorldProviderPocket;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class WorldProviderPersonalPocket extends WorldProviderPocket {
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
        setCloudRenderer(new CloudRenderBlank());
        return new Vec3d(1,1,1);
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            this.lightBrightnessTable[i] = (15);
        }
    }

    @Override
    public double getHorizon() {
        return world.getHeight()-256;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float par1, float par2) {
        return new Vec3d(1,1,1);
    }

    @Override
    public int getActualHeight() {
        return -256;
    }

    @Override
    public String getSaveFolder() {
        return (getDimension() == 0 ? null : "personal");
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.PRIVATE;
    }
}
