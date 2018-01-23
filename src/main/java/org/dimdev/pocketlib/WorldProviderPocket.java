package org.dimdev.pocketlib;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.render.CloudRenderBlank;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class WorldProviderPocket extends WorldProvider {

    @Override
    public void init() {
        hasSkyLight = true;
        generateLightBrightnessTable();
        DimDoors.proxy.setCloudRenderer(this, new CloudRenderBlank());
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorBlank(world, world.getSeed());
    }

    @Override public float calculateCelestialAngle(long worldTime, float partialTicks) { return 0.0F; }

    @Override public boolean canRespawnHere() { return false; }

    @Override public boolean isSurfaceWorld() { return false; }

    @Override public boolean canCoordinateBeSpawn(int x, int z) { return true; } // Spawn is set even if it canCoordinateBeSpawn is false after 1000 tries anyway

    @Override public int getAverageGroundLevel() { return 0; } // Pocket worlds are mostly void-filled

    @Override public boolean shouldMapSpin(String entity, double x, double z, double rotation) { return true; }

    @Override @Nullable
    @SideOnly(Side.CLIENT) public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) { return null; }

    @Override @SideOnly(Side.CLIENT) public boolean doesXZShowFog(int x, int z) {
        return false; // TODO: set this to true outside of pockets
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return Vec3d.ZERO;
    }

    @Override @SideOnly(Side.CLIENT) public Vec3d getFogColor(float celestialAngle, float partialTicks) { return Vec3d.ZERO; }

    @Override
    @SideOnly(Side.CLIENT)
    public double getVoidFogYFactor() {
        return 1;
    }
}
