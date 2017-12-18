package com.zixiken.dimdoors.shared.world.limbodimension;

import com.zixiken.dimdoors.shared.entities.EntityMonolith;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BiomeLimbo extends Biome {

    public BiomeLimbo() {
        super(new Biome.BiomeProperties("Limbo")
                .setRainDisabled()
                .setRainfall(0)
                .setWaterColor(0x000000));

        // topBlock = Blocks.AIR.getDefaultState();
        // fillerBlock = Blocks.AIR.getDefaultState();

        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        spawnableMonsterList.add(new SpawnListEntry(EntityMonolith.class, 100, 4, 4));

        flowers.clear();
    }

    // TODO: move generation here

    @Override public BiomeDecorator createBiomeDecorator() { return null; }

    @Override public void decorate(World worldIn, Random rand, BlockPos pos) {}

    @Override public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) { // TODO: what does this do?
        return super.getSkyColorByTemp(currentTemperature);
    }

    // TODO: check that black/white grass and foliage in getModdedBiomeGrassColor is compatible with other mods such as Quark's greener grass option
    @Override
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return getModdedBiomeGrassColor(0x000000);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return getModdedBiomeFoliageColor(0x000000);
    }
}
