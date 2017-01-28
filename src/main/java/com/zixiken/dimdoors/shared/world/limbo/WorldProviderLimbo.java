package com.zixiken.dimdoors.shared.world.limbo;

import com.zixiken.dimdoors.client.CloudRenderBlank;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderLimbo extends WorldProvider {
    private IRenderHandler skyRenderer;
    //private CustomLimboPopulator spawner;

    public WorldProviderLimbo() {
        this.hasNoSky = false;
        this.skyRenderer = new LimboSkyProvider();
        //this.spawner
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer() {
        return skyRenderer;
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos) {
        return new LimboBiome();
    }

    @Override
    public boolean canRespawnHere()
    {
        return false; //properties.HardcoreLimboEnabled;
    }

    @Override
    public boolean isBlockHighHumidity(BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return false;
    }

    @Override
    protected void generateLightBrightnessTable() {
        float modifier = 0.0F;

        for (int steps = 0; steps <= 15; ++steps) {
            float var3 = 1.0F - steps / 15.0F;
            this.lightBrightnessTable[steps] = ((0.0F + var3) / (var3 * 3.0F + 1.0F) * (1.0F - modifier) + modifier)*3;
            //     System.out.println( this.lightBrightnessTable[steps]+"light");
        }
    }

    @Override
    public BlockPos getSpawnPoint() {
        return this.getRandomizedSpawnPoint();
    }

    @Override
    public float calculateCelestialAngle(long par1, float par3) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public int getMoonPhase(long par1, float par3) {
        return 4;
    }

    @Override
    public String getSaveFolder() {
        return (getDimension() == 0 ? null : "limbo" + getDimension());
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        BlockPos pos = this.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        return world.getBlockState(pos).equals(ModBlocks.blockLimbo.getDefaultState());
    }

    @Override
    public double getHorizon() {
        return world.getHeight()/4-800;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
        setCloudRenderer( new CloudRenderBlank());
        return Vec3d.ZERO;

    }
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float par1, float par2) {
        return new Vec3d(.2, .2, .2);
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return 0;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new LimboGenerator(world, 45);
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return false;
    }

    public static Location getLimboSkySpawn(EntityPlayer player, World world) {
        int x = (int) (player.posX) + MathHelper.clamp(player.world.rand.nextInt(), -100, 100); //-properties.LimboEntryRange, properties.LimboEntryRange);
        int z = (int) (player.posZ) + MathHelper.clamp(player.world.rand.nextInt(), -100, 100); //-properties.LimboEntryRange, properties.LimboEntryRange);
        return new Location(world, x, 700, z);
    }

    @Override
    public BlockPos getRandomizedSpawnPoint() {
        int x = MathHelper.clamp(this.world.rand.nextInt(), -500, 500);
        int z = MathHelper.clamp(this.world.rand.nextInt(), -500, 500);
        return new BlockPos(x, 700, z);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.LIMBO;
    }
}
