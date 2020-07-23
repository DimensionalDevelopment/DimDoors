package org.dimdev.dimdoors.world.limbo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import org.dimdev.dimdoors.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.Blocks.*;
import static org.dimdev.dimdoors.block.ModBlocks.*;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
    private static final Map<Block, Block> DECAY_SEQUENCE = new HashMap<>();

    static {
        BiConsumer<Block, Block> blockBiConsumer = DECAY_SEQUENCE::putIfAbsent;

        blockBiConsumer.accept(STONE, COBBLESTONE);
        blockBiConsumer.accept(COBBLESTONE, END_STONE);
        blockBiConsumer.accept(GRAVEL, SAND);
        blockBiConsumer.accept(SAND, UNRAVELLED_FABRIC);
        blockBiConsumer.accept(GLASS, SAND);
        blockBiConsumer.accept(GRASS_BLOCK, DIRT);
        blockBiConsumer.accept(DIRT, SAND);
        blockBiConsumer.accept(REDSTONE_BLOCK, REDSTONE_ORE);
        blockBiConsumer.accept(REDSTONE_ORE, STONE);
        blockBiConsumer.accept(EMERALD_BLOCK, EMERALD_ORE);
        blockBiConsumer.accept(EMERALD_ORE, STONE);
        blockBiConsumer.accept(COAL_BLOCK, COAL_ORE);
        blockBiConsumer.accept(COAL_ORE, STONE);
        blockBiConsumer.accept(IRON_BLOCK, IRON_ORE);
        blockBiConsumer.accept(IRON_ORE, STONE);
        blockBiConsumer.accept(LAPIS_BLOCK, LAPIS_ORE);
        blockBiConsumer.accept(LAPIS_ORE, STONE);
        blockBiConsumer.accept(GOLD_BLOCK, GOLD_ORE);
        blockBiConsumer.accept(GOLD_ORE, STONE);
        blockBiConsumer.accept(SANDSTONE, SAND);
        blockBiConsumer.accept(END_STONE_BRICKS, END_STONE);
        blockBiConsumer.accept(GRASS_PATH, DIRT);
        blockBiConsumer.accept(POLISHED_GRANITE, GRANITE);
        blockBiConsumer.accept(GRANITE, DIORITE);
        blockBiConsumer.accept(POLISHED_ANDESITE, ANDESITE);
        blockBiConsumer.accept(ANDESITE, DIORITE);
        blockBiConsumer.accept(POLISHED_DIORITE, DIORITE);
        blockBiConsumer.accept(GRANITE, DIORITE);
        blockBiConsumer.accept(DIORITE, COBBLESTONE);
        blockBiConsumer.accept(POLISHED_BLACKSTONE, BLACKSTONE);
        blockBiConsumer.accept(BLACKSTONE, COBBLESTONE);
        blockBiConsumer.accept(PODZOL, DIRT);
        blockBiConsumer.accept(FARMLAND, DIRT);
        blockBiConsumer.accept(STONE_BRICKS, CRACKED_STONE_BRICKS);
        blockBiConsumer.accept(CRACKED_STONE_BRICKS, DIORITE);
        blockBiConsumer.accept(END_STONE, SAND);
        blockBiConsumer.accept(OAK_LOG, OAK_PLANKS);
        blockBiConsumer.accept(BIRCH_LOG, BIRCH_PLANKS);
        blockBiConsumer.accept(SPRUCE_LOG, SPRUCE_PLANKS);
        blockBiConsumer.accept(JUNGLE_LOG, JUNGLE_PLANKS);
        blockBiConsumer.accept(ACACIA_LOG, ACACIA_PLANKS);
        blockBiConsumer.accept(DARK_OAK_LOG, DARK_OAK_PLANKS);
        blockBiConsumer.accept(OAK_WOOD, OAK_LOG);
        blockBiConsumer.accept(BIRCH_WOOD, BIRCH_LOG);
        blockBiConsumer.accept(SPRUCE_WOOD, SPRUCE_LOG);
        blockBiConsumer.accept(JUNGLE_WOOD, JUNGLE_LOG);
        blockBiConsumer.accept(ACACIA_WOOD, ACACIA_LOG);
        blockBiConsumer.accept(DARK_OAK_WOOD, DARK_OAK_LOG);
    }

    private static final Random random = new Random();
    private static Block[] blocksImmuneToDecay = null;

    public static Map<Block, Block> getDecaySequence() {
        return DECAY_SEQUENCE;
    }

    public static Block[] getBlocksImmuneToDecay() {
        if (blocksImmuneToDecay == null) {
            blocksImmuneToDecay = new Block[]{
                    UNRAVELLED_FABRIC,
                    ETERNAL_FLUID,
                    DIMENSIONAL_PORTAL,
                    IRON_DIMENSIONAL_DOOR,
                    OAK_DIMENSIONAL_DOOR,
                    DETACHED_RIFT,
                    GOLD_DOOR,
                    QUARTZ_DOOR,
                    GOLD_DIMENSIONAL_DOOR
            };
        }

        return blocksImmuneToDecay;
    }

    /**
     * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
     * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
     */
    public static void applySpreadDecay(World world, BlockPos pos) {
        //Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
        //full spread decay checks, which can also shift its performance impact on the game.
        if (random.nextDouble() < ModConfig.LIMBO.decaySpreadChance) {
            //Apply decay to the blocks above, below, and on all four sides.
            //World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
            decayBlock(world, pos.up());
            decayBlock(world, pos.down());
            decayBlock(world, pos.north());
            decayBlock(world, pos.south());
            decayBlock(world, pos.west());
            decayBlock(world, pos.east());
        }
    }

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    private static boolean decayBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (canDecayBlock(state, world, pos)) {
            //Loop over the block IDs that decay can go through.
            //Find an index matching the current blockID, if any.

            if (getDecaySequence().containsKey(state.getBlock())) {
                Block decay = getDecaySequence().get(state.getBlock());
                world.setBlockState(pos, decay.getDefaultState());
            } else if (!state.isFullCube(world, pos)) {
                world.setBlockState(pos, AIR.getDefaultState());
            }
            return true;
        }

        return false;
    }

    /**
     * Checks if a block can decay. We will not decay air, certain DD blocks, or containers.
     */
    private static boolean canDecayBlock(BlockState state, World world, BlockPos pos) {
        if (world.isAir(pos)) {
            return false;
        }

        for (int k = 0; k < getBlocksImmuneToDecay().length; k++) {
            if (state.getBlock().equals(getBlocksImmuneToDecay()[k])) {
                return false;
            }
        }

        return !(state.getBlock() instanceof BlockWithEntity);
    }
}
