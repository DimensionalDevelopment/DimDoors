package org.dimdev.dimdoors.shared.world.pocketdimension;

import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BiomeBlank extends Biome {

    private final boolean white;

    public BiomeBlank(boolean white, boolean monoliths) { // TODO: split this class
        super(new BiomeProperties((monoliths ? "Dangerous " : "") + (white ? "White" : "Black") + " Void")
                .setBaseHeight(0F)
                .setHeightVariation(0F)
                .setRainDisabled()
                .setRainfall(0)
                .setWaterColor(white ? 0xFFFFFF : 0x111111));
        this.white = white;

        topBlock = Blocks.AIR.getDefaultState();
        fillerBlock = Blocks.AIR.getDefaultState();

        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        if (monoliths) spawnableMonsterList.add(new SpawnListEntry(EntityMonolith.class, 100, 4, 4));

        flowers.clear();
    }

    @Override public BiomeDecorator createBiomeDecorator() { return null; } // For efficiency

    @Override public void decorate(World world, Random rand, BlockPos pos) {}

    @Override public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int x, int z, double noiseVal) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) {
        return white ? 0xFCFCFC : 0x000000; // https://bugs.mojang.com/projects/MC/issues/MC-123703
    }

    // TODO: check that black/white grass and foliage in getModdedBiomeGrassColor is compatible with other mods such as Quark's greener grass option
    @Override
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return getModdedBiomeGrassColor(white ? 0xFFFFFF : 0x111111);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return getModdedBiomeFoliageColor(white ? 0xFFFFFF : 0x111111);
    }
}
