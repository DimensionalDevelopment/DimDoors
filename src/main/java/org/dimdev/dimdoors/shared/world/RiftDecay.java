package org.dimdev.dimdoors.shared.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
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
            for(String blocked : ModConfig.general.blockRiftDecayBlackList) {
                Block fromString = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blocked));
                if(fromString!=null) temp.add(fromString);
                else DimDoors.log.error("Invalid block name for rift decay blacklist! \""+blocked+"\"");
            }
            blocksImmuneToDecay = temp.toArray(new Block[0]);
        }

        return blocksImmuneToDecay;
    }

    /**
     * Checks the blocks orthogonally around the location of the floating rift.
     */
    public static void applySpreadDecay(World world, BlockPos pos, TileEntityFloatingRift rift) {
        //Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
        //full spread decay checks, which can also shift its performance impact on the game.
        float chance = rift.size/100f;
        if (random.nextInt(MAX_DECAY_CHANCE) <= chance) {
            //Apply decay to the block the rift occupies
            //World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
            if(rift.size<10000f) {
                decayBlock(world, pos);
                //Once the rift grows beyond a certain point, it can begin breaking blocks around it as well
                //This helps with the image of the rift growing more unstable as it is left to do as it pleases in the world
                //Yeah, I know the random stuff shouldn't be hardcoded like this. I will make it a real formula soon
                int rand = random.nextInt(6);
                if (rand < 1) decayBlock(world, pos.up());
                else if (rand < 2) decayBlock(world, pos.down());
                else if (rand < 3) decayBlock(world, pos.north());
                else if (rand < 4) decayBlock(world, pos.south());
                else if (rand < 5) decayBlock(world, pos.west());
                else decayBlock(world, pos.east());
            } else {
                //TODO - try and account for larger areas around the rift as it grows even larger without impacting performance
                //This is just a copy & paste for now
                int rand = random.nextInt(6);
                if(rand<1) decayBlock(world, pos.up());
                else if(rand<2) decayBlock(world, pos.down());
                else if(rand<3) decayBlock(world, pos.north());
                else if(rand<4) decayBlock(world, pos.south());
                else if(rand<5) decayBlock(world, pos.west());
                else decayBlock(world, pos.east());
            }
        }
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
            world.spawnEntity(new EntityItem(world,pos.getX(),pos.getY(),pos.getZ(),ModItems.WORLD_THREAD.getDefaultInstance()));
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
