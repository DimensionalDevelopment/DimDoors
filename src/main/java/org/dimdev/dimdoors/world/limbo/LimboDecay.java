package org.dimdev.dimdoors.world.limbo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.minecraft.block.Blocks.*;
import static org.dimdev.dimdoors.block.ModBlocks.*;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
    private static final Map<Block, Block> DECAY_SEQUENCE = new HashMap<>();

    static { // TODO: make this work on the server
        try {
            for (Resource resource : MinecraftClient.getInstance().getResourceManager().getAllResources(new Identifier("dimdoors:limbo_decay"))) {
                Map<String, String> decays = new Gson().fromJson(new InputStreamReader(resource.getInputStream()), new TypeToken<Map<String, String>>() {}.getType());

                for (Map.Entry<String, String> decay : decays.entrySet()) {
                    DECAY_SEQUENCE.put(Registry.BLOCK.get(new Identifier(decay.getKey())), Registry.BLOCK.get(new Identifier(decay.getValue())));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    ETERNAL_FABRIC,
                    DIMENSIONAL_PORTAL,
                    IRON_DIMENSIONAL_DOOR,
                    WOOD_DIMENSIONAL_DOOR,
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
