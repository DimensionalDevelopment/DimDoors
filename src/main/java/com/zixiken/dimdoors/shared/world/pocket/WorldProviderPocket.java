package com.zixiken.dimdoors.shared.world.pocket;

import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPocket extends WorldProvider {
    //protected CustomLimboPopulator spawner;
    protected IRenderHandler skyRenderer;

    public WorldProviderPocket() {
        this.hasNoSky = true;
    }


    @Override
    public String getSaveFolder() {
        return (getDimension() == 0 ? null : "private");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        setCloudRenderer( new CloudRenderBlank());
        return Vec3d.ZERO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float par1, float par2) {
        return Vec3d.ZERO;
    }

    @Override
    public double getHorizon() {
        return world.getHeight();
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new PocketGenerator(world, 0); //, spawner);
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean light) {
        return false;
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return false;
    }

    public float calculateCelestialAngle(long par1, float par3) {
        return .5F;
    }

    @Override
    protected void generateLightBrightnessTable() {
        for (int steps = 0; steps <= 15; ++steps) {
            float var3 = (float) (Math.pow(steps,1.5) / Math.pow(15.0F,1.5));
            this.lightBrightnessTable[15-steps] = var3;
            System.out.println( this.lightBrightnessTable[steps]+"light");
        }
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return getDimension();
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public int getActualHeight() {
        return 256;
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.DUNGEON;
    }
}
