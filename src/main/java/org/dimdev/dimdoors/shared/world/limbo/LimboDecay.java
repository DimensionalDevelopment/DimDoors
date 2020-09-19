package org.dimdev.dimdoors.shared.world.limbo;

import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeChunkManager;
import org.apache.logging.log4j.util.TriConsumer;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.world.ModDimensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import static net.minecraft.init.Blocks.*;
import static org.dimdev.dimdoors.shared.blocks.ModBlocks.*;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {

    private static final int MAX_DECAY_SPREAD_CHANCE = 100;
    private static final int DECAY_SPREAD_CHANCE = 50;
    private static final int CHUNK_SIZE = 16;
    private static final int SECTION_HEIGHT = 16;

    //Provides a reversed list of the block IDs that blocks cycle through during decay.
    private static Map<IBlockState, IBlockState> decaySequence = null;

    private static final Random random = new Random();
    private static Block[] blocksImmuneToDecay = null;

    public static Map<IBlockState, IBlockState> getDecaySequence() {
        return decaySequence;
    }

    public static Block[] getBlocksImmuneToDecay() {
        if (blocksImmuneToDecay == null) {
            blocksImmuneToDecay = new Block[]{
                    UNRAVELLED_FABRIC,
                    ETERNAL_FABRIC,
                    DIMENSIONAL_PORTAL,
                    IRON_DIMENSIONAL_DOOR,
                    WARP_DIMENSIONAL_DOOR,
                    RIFT,
                    GOLD_DOOR,
                    QUARTZ_DOOR,
                    GOLD_DIMENSIONAL_DOOR,
                    BLOCK_SOLID_STATIC
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
        if (random.nextInt(MAX_DECAY_SPREAD_CHANCE) < DECAY_SPREAD_CHANCE) {
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
     * Picks random blocks from each active chunk in Limbo and, if decay is applicable, converts them directly to Unraveled Fabric.
     * This decay method is designed to stop players from avoiding Limbo decay by building floating structures.
     */
    public static void applyRandomFastDecay() {
        int sectionY;
        int limboHeight;
        int[] limbo = DimensionManager.getDimensions(ModDimensions.LIMBO);

        for (int i : limbo) {
            World world = DimensionManager.getWorld(i);

            limboHeight = world.getHeight();

            //Obtain the coordinates of active chunks in Limbo. For each section of each chunk,
            //pick a random block and try to apply fast decay.
            for (ChunkPos chunkPos : ForgeChunkManager.getPersistentChunksFor(world).keySet()) {
                //Loop through each chunk section and fast-decay a random block
                //Apply the changes using the world object instead of directly to the chunk so that clients are always notified.
                for (sectionY = 0; sectionY < limboHeight; sectionY += SECTION_HEIGHT) {
                    BlockPos pos = new BlockPos(
                            chunkPos.x * CHUNK_SIZE + random.nextInt(CHUNK_SIZE),
                            chunkPos.z * CHUNK_SIZE + random.nextInt(CHUNK_SIZE),
                            sectionY + random.nextInt(SECTION_HEIGHT));
                    decayBlockFast(world, pos);
                }
            }
        }
    }

    /**
     * Checks if a block can be decayed and, if so, changes it directly into Unraveled Fabric.
     */
    private static boolean decayBlockFast(World world, BlockPos pos) {
        IBlockState block = world.getBlockState(pos);
        if (canDecayBlock(block, world, pos)) {
            if (block.isNormalCube()) {
                world.setBlockState(pos, UNRAVELLED_FABRIC.getDefaultState());
            } else {
                world.setBlockState(pos, AIR.getDefaultState());
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    private static boolean decayBlock(World world, BlockPos pos) {
        IBlockState block = world.getBlockState(pos);
        if (canDecayBlock(block, world, pos)) {
            //Loop over the block IDs that decay can go through.
            //Find an index matching the current blockID, if any.

            if(getDecaySequence().containsKey(block)) {
                IBlockState decay = getDecaySequence().get(block);
                world.setBlockState(pos, decay);
            } else if (!block.isNormalCube()) {
                world.setBlockState(pos, AIR.getDefaultState());
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if a block can decay. We will not decay air, certain DD blocks, or containers.
     */
    private static boolean canDecayBlock(IBlockState state, World world, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return false;
        }

        for (int k = 0; k < getBlocksImmuneToDecay().length; k++) {
            if (state.getBlock().equals(getBlocksImmuneToDecay()[k])) {
                return false;
            }
        }

        return !(state instanceof BlockContainer);
    }

    static {
        decaySequence = new HashMap<>();

        BiConsumer<IBlockState, IBlockState> stateConsumer = (a,b) -> decaySequence.put(a,b);
        BiConsumer<Block, Block> blockConsumer = (a,b) -> stateConsumer.accept(a.getDefaultState(), b.getDefaultState());

        blockConsumer.accept(STONE, COBBLESTONE);
        blockConsumer.accept(COBBLESTONE, END_STONE);
        blockConsumer.accept(GRAVEL, SAND);
        blockConsumer.accept(SAND, UNRAVELLED_FABRIC);
        blockConsumer.accept(GLASS, SAND);
        blockConsumer.accept(GRASS, DIRT);
        blockConsumer.accept(DIRT, SAND);

        for (BlockPlanks.EnumType planks : BlockPlanks.EnumType.values()) {
            IBlockState plank = PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, planks);

            if(planks.getMetadata() < 4)
            stateConsumer.accept(
                    LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, planks),
                    plank);
            else stateConsumer.accept(
                    LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, planks),
                    plank);
            stateConsumer.accept(plank, GLASS.getDefaultState());
        }

        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH),
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH),
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH),
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        stateConsumer.accept(
                STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
                COBBLESTONE.getDefaultState());

        for (EnumDyeColor color : EnumDyeColor.values()) {
            stateConsumer.accept(
                    STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, color),
                    GLASS.getDefaultState());
            stateConsumer.accept(
                    CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, color),
                    CONCRETE_POWDER.getDefaultState().withProperty(BlockConcretePowder.COLOR, color));
            stateConsumer.accept(
                    CONCRETE_POWDER.getDefaultState().withProperty(BlockConcretePowder.COLOR, color),
                    SAND.getDefaultState());
        }

        blockConsumer.accept(END_STONE, SAND);

        stateConsumer.accept(
                STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED),
                STONEBRICK.getDefaultState());
        stateConsumer.accept(
                STONEBRICK.getDefaultState(),
                COBBLESTONE.getDefaultState());
        blockConsumer.accept(REDSTONE_BLOCK, REDSTONE_ORE);
        blockConsumer.accept(LIT_REDSTONE_ORE, STONE);
        blockConsumer.accept(REDSTONE_ORE, STONE);
        blockConsumer.accept(EMERALD_BLOCK, EMERALD_ORE);
        blockConsumer.accept(EMERALD_ORE, STONE);
        blockConsumer.accept(COAL_BLOCK, COAL_ORE);
        blockConsumer.accept(COAL_ORE, STONE);
        blockConsumer.accept(IRON_BLOCK, IRON_ORE);
        blockConsumer.accept(IRON_ORE, STONE);
        blockConsumer.accept(LAPIS_BLOCK, LAPIS_ORE);
        blockConsumer.accept(LAPIS_ORE, STONE);
        blockConsumer.accept(GOLD_BLOCK, GOLD_ORE);
        blockConsumer.accept(GOLD_ORE, STONE);
        blockConsumer.accept(SANDSTONE, SAND);
        blockConsumer.accept(END_BRICKS, END_STONE);
        blockConsumer.accept(GRASS_PATH, DIRT);
        stateConsumer.accept(DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL), DIRT.getDefaultState());
        blockConsumer.accept(FARMLAND, DIRT);
    }
}
