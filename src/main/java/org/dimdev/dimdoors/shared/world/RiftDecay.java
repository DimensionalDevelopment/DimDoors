package org.dimdev.dimdoors.shared.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.ArrayList;
import java.util.Random;

/**
 * Provides methods for applying Rift decay, which turns blocks eaten by rifts into world thread
 */
public class RiftDecay {
    private static final int MAX_DECAY_CHANCE = 5000;
    private static final Random random = new Random();
    public static Block[] blocksImmuneToDecay = null;

    public static Block[] getBlocksImmuneToDecay() {
        if (blocksImmuneToDecay == null) {
            ArrayList<Block> temp = new ArrayList<>();
            temp.add(ModBlocks.UNRAVELLED_FABRIC);
            temp.add(ModBlocks.ETERNAL_FABRIC);
            temp.add(ModBlocks.DIMENSIONAL_PORTAL);
            temp.add(ModBlocks.IRON_DIMENSIONAL_DOOR);
            temp.add(ModBlocks.WARP_DIMENSIONAL_DOOR);
            temp.add(ModBlocks.RIFT);
            temp.add(ModBlocks.GOLD_DOOR);
            temp.add(ModBlocks.QUARTZ_DOOR);
            temp.add(ModBlocks.GOLD_DIMENSIONAL_DOOR);
            for(String blocked : ModConfig.rifts.blockRiftDecayBlackList) {
                Block fromString = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blocked));
                if(fromString!=null) temp.add(fromString);
                else DimDoors.log.error("Invalid block name for rift decay blacklist! \""+blocked+"\"");
            }
            blocksImmuneToDecay = temp.toArray(new Block[0]);
        }

        return blocksImmuneToDecay;
    }

    /**
     * Checks the blocks around the location of the floating rift and applies the decay
     */
    public static void applySpreadDecay(World world, BlockPos pos, TileEntityFloatingRift rift) {
        //Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
        //full spread decay checks, which can also shift its performance impact on the game.
        float chance = rift.size/100f;
        if ((random.nextFloat()*MAX_DECAY_CHANCE) <= chance) {
            BlockPos selected = getRandomPosInRange(pos,chance/2f);
            //Apply decay to a random block around the rift and the rift itself just in case
            //World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
            decayBlock(world, selected);
            decayBlock(world, pos);
        }
    }

    /**
     * Gets a random block pos around the input.
     */
    private static BlockPos getRandomPosInRange(BlockPos center, float range) {
        double x = (random.nextDouble()-0.5d)*range;
        double y = (random.nextDouble()-0.5d)*range;
        double z = (random.nextDouble()-0.5d)*range;
        return center.add((x > 0) ? Math.ceil(x) : Math.floor(x),(y > 0) ? Math.ceil(y) : Math.floor(y),(z > 0) ? Math.ceil(z) : Math.floor(z));
    }

    /**
     * Checks if a block can be decayed and, if so, changes it into world thread.
     */
    private static boolean decayBlock(World world, BlockPos pos) {
        IBlockState block = world.getBlockState(pos);
        //Check the decaying block against the blacklist and remove it as necessary
        if (canDecayBlock(block, world, pos)) {
            //change block to air and spawn a new world thread item
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            if (ModConfig.rifts.enableDecayDrops) {
                world.spawnEntity(new EntityItem(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(ModItems.WORLD_THREAD)));
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if a block can decay. We will not decay air, certain DD blocks, containers, or blacklisted blocks.
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
}
