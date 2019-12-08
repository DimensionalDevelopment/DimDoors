package org.dimdev.dimdoors.world.limbo;

import org.dimdev.dimdoors.entity.MonolithEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
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
        spawnableMonsterList.add(new SpawnListEntry(MonolithEntity.class, 100, 1, 1));

        flowers.clear();

        decorator.extraTreeChance = 0;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.gravelPatchesPerChunk = 0;
        decorator.sandPatchesPerChunk = 0;
        decorator.clayPerChunk = 0;
        decorator.generateFalls = false;
    }

    // Some mods like RFTools rely on the decorator being present, so we need to create one even if we don't use it.
    //@Override public BiomeDecorator createBiomeDecorator() { return null; }

    @Override public void decorate(World world, Random rand, BlockPos pos) {}

    @Override public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int x, int z, double noiseVal) {}

    // TODO: check that black/white grass and foliage in getModdedBiomeGrassColor is compatible with other mods such as Quark's greener grass option
    @Override
    @Environment(EnvType.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return getModdedBiomeGrassColor(0x000000);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return getModdedBiomeFoliageColor(0x000000);
    }

    @Override
    public float getSpawningChance() {
        return 0.1F;
    }
}
