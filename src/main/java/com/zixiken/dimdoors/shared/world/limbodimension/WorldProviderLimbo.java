package com.zixiken.dimdoors.shared.world.limbodimension;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.blocks.BlockFabric;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import com.zixiken.dimdoors.shared.world.ModBiomes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderLimbo extends WorldProvider {

    @Override
    public void init() {
        hasSkyLight = false;
        biomeProvider = new BiomeProviderSingle(ModBiomes.LIMBO);
        DimDoors.proxy.setCloudRenderer(this, new CloudRenderBlank());
        DimDoors.proxy.setSkyRenderer(this, new LimboSkyProvider());
    }

    @Override
    public boolean canRespawnHere() {
        return false; // TODO: properties.HardcoreLimboEnabled;
    }

    @Override
    protected void generateLightBrightnessTable() {
        float modifier = 0.0F;

        for (int steps = 0; steps <= 15; ++steps) {
            float var3 = 1.0F - steps / 15.0F;
            lightBrightnessTable[steps] = ((0.0F + var3) / (var3 * 3.0F + 1.0F) * (1.0F - modifier) + modifier) * 3;
        }
    }

    @Override
    public BlockPos getSpawnPoint() {
        return getRandomizedSpawnPoint();
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getMoonPhase(long worldTime) {
        return 4;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        return world.getBlockState(pos).equals(ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.UNRAVELED));
    }

    @Override
    public double getHorizon() {
        return (double) world.getHeight() / 4 - 800;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return 0;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new LimboGenerator(world, world.getSeed());
    }

    public static Location getLimboSkySpawn(EntityPlayer player) {
        int x = (int) player.posX + MathHelper.clamp(player.world.rand.nextInt(), -100, 100); // TODO: -properties.LimboEntryRange, properties.LimboEntryRange);
        int z = (int) player.posZ + MathHelper.clamp(player.world.rand.nextInt(), -100, 100); // TODO: -properties.LimboEntryRange, properties.LimboEntryRange);
        return new Location(DimDoorDimensions.LIMBO.getId(), x, 700, z);
    }

    @Override
    public BlockPos getRandomizedSpawnPoint() {
        int x = MathHelper.clamp(world.rand.nextInt(), -500, 500);
        int z = MathHelper.clamp(world.rand.nextInt(), -500, 500);
        return new BlockPos(x, 700, z);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.LIMBO;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return Vec3d.ZERO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return new Vec3d(.2, .2, .2);
    }
}
