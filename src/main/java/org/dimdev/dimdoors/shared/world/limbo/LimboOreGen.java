package org.dimdev.dimdoors.shared.world.limbo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

import java.util.Random;

public class LimboOreGen implements IWorldGenerator {

    private Random random;
    private int chunkX;
    private int chunkZ;
    private World world;
    private IChunkGenerator chunkGenerator;
    private IChunkProvider chunkProvider;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        this.random = random;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.world = world;
        this.chunkGenerator = chunkGenerator;
        this.chunkProvider = chunkProvider;

        switch(world.provider.getDimension()) {

            case 684:

                generateLimbo(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;

            default:
                break;

        }
    }

    private void generateLimbo(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    {
        runGenerator(ModBlocks.BLOCK_SOLID_STATIC.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 5, 254, random.nextInt(5) + 3,  65);

    }


    private void runGenerator(IBlockState ore, World world, Random random, int x, int z, int minHeight, int maxHeight, int size, int chance)
    {
        if(minHeight > maxHeight || minHeight < 0 || maxHeight > 256) throw new IllegalArgumentException("Ore generated out of bounds.");

            int deltaY = maxHeight - minHeight;

            for(int i = 0; i < chance; i++)
            {
                BlockPos pos = new BlockPos(x + random.nextInt(16), minHeight + random.nextInt(deltaY), z + random.nextInt(16));

                WorldGenMinable generator  = new WorldGenMinable(ore, size, BlockMatcher.forBlock(ModBlocks.UNRAVELLED_FABRIC));
                generator.generate(world, random, pos);
            }

    }

}
