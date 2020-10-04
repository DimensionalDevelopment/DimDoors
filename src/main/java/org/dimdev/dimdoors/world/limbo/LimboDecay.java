package org.dimdev.dimdoors.world.limbo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.ModConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import static org.dimdev.dimdoors.block.ModBlocks.DETACHED_RIFT;
import static org.dimdev.dimdoors.block.ModBlocks.DIMENSIONAL_PORTAL;
import static org.dimdev.dimdoors.block.ModBlocks.ETERNAL_FLUID;
import static org.dimdev.dimdoors.block.ModBlocks.GOLD_DIMENSIONAL_DOOR;
import static org.dimdev.dimdoors.block.ModBlocks.GOLD_DOOR;
import static org.dimdev.dimdoors.block.ModBlocks.IRON_DIMENSIONAL_DOOR;
import static org.dimdev.dimdoors.block.ModBlocks.OAK_DIMENSIONAL_DOOR;
import static org.dimdev.dimdoors.block.ModBlocks.QUARTZ_DOOR;
import static org.dimdev.dimdoors.block.ModBlocks.UNRAVELLED_FABRIC;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.loader.api.FabricLoader;
import static net.minecraft.block.Blocks.ACACIA_LOG;
import static net.minecraft.block.Blocks.ACACIA_PLANKS;
import static net.minecraft.block.Blocks.ACACIA_WOOD;
import static net.minecraft.block.Blocks.AIR;
import static net.minecraft.block.Blocks.ANDESITE;
import static net.minecraft.block.Blocks.BIRCH_LOG;
import static net.minecraft.block.Blocks.BIRCH_PLANKS;
import static net.minecraft.block.Blocks.BIRCH_WOOD;
import static net.minecraft.block.Blocks.BLACKSTONE;
import static net.minecraft.block.Blocks.COAL_BLOCK;
import static net.minecraft.block.Blocks.COAL_ORE;
import static net.minecraft.block.Blocks.COBBLESTONE;
import static net.minecraft.block.Blocks.CRACKED_STONE_BRICKS;
import static net.minecraft.block.Blocks.DARK_OAK_LOG;
import static net.minecraft.block.Blocks.DARK_OAK_PLANKS;
import static net.minecraft.block.Blocks.DARK_OAK_WOOD;
import static net.minecraft.block.Blocks.DIORITE;
import static net.minecraft.block.Blocks.DIRT;
import static net.minecraft.block.Blocks.EMERALD_BLOCK;
import static net.minecraft.block.Blocks.EMERALD_ORE;
import static net.minecraft.block.Blocks.END_STONE;
import static net.minecraft.block.Blocks.END_STONE_BRICKS;
import static net.minecraft.block.Blocks.FARMLAND;
import static net.minecraft.block.Blocks.GLASS;
import static net.minecraft.block.Blocks.GOLD_BLOCK;
import static net.minecraft.block.Blocks.GOLD_ORE;
import static net.minecraft.block.Blocks.GRANITE;
import static net.minecraft.block.Blocks.GRASS_BLOCK;
import static net.minecraft.block.Blocks.GRASS_PATH;
import static net.minecraft.block.Blocks.GRAVEL;
import static net.minecraft.block.Blocks.IRON_BLOCK;
import static net.minecraft.block.Blocks.IRON_ORE;
import static net.minecraft.block.Blocks.JUNGLE_LOG;
import static net.minecraft.block.Blocks.JUNGLE_PLANKS;
import static net.minecraft.block.Blocks.JUNGLE_WOOD;
import static net.minecraft.block.Blocks.LAPIS_BLOCK;
import static net.minecraft.block.Blocks.LAPIS_ORE;
import static net.minecraft.block.Blocks.OAK_LOG;
import static net.minecraft.block.Blocks.OAK_PLANKS;
import static net.minecraft.block.Blocks.OAK_WOOD;
import static net.minecraft.block.Blocks.PODZOL;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE;
import static net.minecraft.block.Blocks.POLISHED_BLACKSTONE;
import static net.minecraft.block.Blocks.POLISHED_DIORITE;
import static net.minecraft.block.Blocks.POLISHED_GRANITE;
import static net.minecraft.block.Blocks.REDSTONE_BLOCK;
import static net.minecraft.block.Blocks.REDSTONE_ORE;
import static net.minecraft.block.Blocks.SAND;
import static net.minecraft.block.Blocks.SANDSTONE;
import static net.minecraft.block.Blocks.SPRUCE_LOG;
import static net.minecraft.block.Blocks.SPRUCE_PLANKS;
import static net.minecraft.block.Blocks.SPRUCE_WOOD;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.STONE_BRICKS;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final UnboundedMapCodec<Block, Block> CODEC = Codec.unboundedMap(Identifier.CODEC.xmap(Registry.BLOCK::get, Registry.BLOCK::getId), Registry.BLOCK);
    private static final Consumer<String> STDERR = System.err::println;
    private static final Map<Block, Block> DECAY_SEQUENCE = new HashMap<>();
    private static final Map<Block, Block> DEFAULT_VALUES;
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dimdoors_limbo_decay.json");

    public static void init() {
        try {
            JsonObject configObject = null;
            if (Files.isDirectory(CONFIG_PATH)) {
                Files.delete(CONFIG_PATH);
            }
            if (!Files.exists(CONFIG_PATH)) {
                Files.createFile(CONFIG_PATH);
                JsonObject jsonObject = CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT_VALUES).getOrThrow(false, STDERR).getAsJsonObject();
                configObject = jsonObject;
                String json = GSON.toJson(jsonObject);
                Files.write(CONFIG_PATH, json.getBytes());
                DECAY_SEQUENCE.clear();
                DECAY_SEQUENCE.putAll(DEFAULT_VALUES);
            }
            if (configObject == null) {
                try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    configObject = GSON.fromJson(reader, JsonObject.class);
                    Map<Block, Block> blocks = CODEC.decode(JsonOps.INSTANCE, configObject).getOrThrow(false, STDERR).getFirst();
                    DECAY_SEQUENCE.clear();
                    DEFAULT_VALUES.putAll(blocks);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (random.nextDouble() < ModConfig.INSTANCE.getLimboConfig().decaySpreadChance) {
            //Apply decay to the blocks above, below, and on all four sides.
            //World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
            boolean flag = decayBlock(world, pos.up());
            flag = flag && decayBlock(world, pos.down());
            flag = flag && decayBlock(world, pos.north());
            flag = flag && decayBlock(world, pos.south());
            flag = flag && decayBlock(world, pos.west());
            flag = flag && decayBlock(world, pos.east());
            if (flag) {
                LOGGER.debug("Applied limbo decay to block at all six sides at position {} in dimension {}", pos, world.getRegistryKey().getValue());
            }
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

    static {
        ImmutableMap.Builder<Block, Block> builder = ImmutableMap.builder();
        BiConsumer<Block, Block> blockBiConsumer = builder::put;

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

        DEFAULT_VALUES = builder.build();
    }

}
