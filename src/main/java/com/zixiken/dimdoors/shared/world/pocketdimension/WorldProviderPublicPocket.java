package com.zixiken.dimdoors.shared.world.pocketdimension;

import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPublicPocket extends WorldProvider { //@todo, we might want an abstract super class to this one?

    //protected CustomLimboPopulator spawner;
    protected IRenderHandler skyRenderer;

    public WorldProviderPublicPocket() {
        hasSkyLight = true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        setCloudRenderer(new CloudRenderBlank());
        return Vec3d.ZERO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return Vec3d.ZERO;
    }

    @Override
    public double getHorizon() {
        return world.getHeight();
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new PocketChunkGenerator(world, 0); //, spawner);
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return false;
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return false;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return .5F;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    /*@Override
    protected void generateLightBrightnessTable() {
        for (int steps = 0; steps <= 15; ++steps) {
            float var3 = (float) (Math.pow(steps, 1.5) / Math.pow(15.0F, 1.5));
            this.lightBrightnessTable[15 - steps] = var3;
            System.out.println(this.lightBrightnessTable[steps] + "light");
        }
    }*/

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public int getActualHeight() {
        return 256;
    }

    public EnumPocketType getPocketType() {
        return EnumPocketType.PUBLIC;
    }

    @Override
    public String getSaveFolder() {
        return "DIM" + getDimension() + "DimDoorsPublic";
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.getPocketDimensionType(getPocketType());
    }
}
