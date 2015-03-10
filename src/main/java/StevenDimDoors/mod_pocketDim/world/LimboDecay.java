package StevenDimDoors.mod_pocketDim.world;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public class LimboDecay {

	private static final int MAX_DECAY_SPREAD_CHANCE = 100;
	private static final int DECAY_SPREAD_CHANCE = 50;
	private static final int CHUNK_SIZE = 16;
	private static final int SECTION_HEIGHT = 16;

	//Provides a reversed list of the block IDs that blocks cycle through during decay.
	private Block[] decaySequence = null;
	
	private final Random random;
	private final DDProperties properties;
	private Block[] blocksImmuneToDecay = null;
	
	public LimboDecay(DDProperties properties)
	{
		this.properties = properties;
		this.random = new Random();
	}

    public Block[] getDecaySequence() {
        if (decaySequence == null) {
            decaySequence = new Block[] {
                    mod_pocketDim.blockLimbo,
                    Blocks.gravel,
                    Blocks.cobblestone,
                    Blocks.stone
            };
        }

        return decaySequence;
    }

    public Block[] getBlocksImmuneToDecay() {
        if (blocksImmuneToDecay == null) {
            blocksImmuneToDecay = new Block[] {
                    mod_pocketDim.blockLimbo,
                    mod_pocketDim.blockDimWallPerm,
                    mod_pocketDim.transientDoor,
                    mod_pocketDim.dimensionalDoor,
                    mod_pocketDim.warpDoor,
                    mod_pocketDim.blockRift,
                    mod_pocketDim.unstableDoor,
                    mod_pocketDim.goldenDoor,
                    mod_pocketDim.goldenDimensionalDoor
            };
        }

        return blocksImmuneToDecay;
    }

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public void applySpreadDecay(World world, int x, int y, int z)
	{		
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (random.nextInt(MAX_DECAY_SPREAD_CHANCE) < DECAY_SPREAD_CHANCE)
		{
			//Apply decay to the blocks above, below, and on all four sides.
			//World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
			decayBlock(world, x - 1, y, z);
			decayBlock(world, x + 1, y, z);
			decayBlock(world, x, y, z - 1);
			decayBlock(world, x, y, z + 1);
			decayBlock(world, x, y - 1, z);
			decayBlock(world, x, y + 1, z);
		}
	}
	
	/**
	 * Picks random blocks from each active chunk in Limbo and, if decay is applicable, converts them directly to Unraveled Fabric.
	 * This decay method is designed to stop players from avoiding Limbo decay by building floating structures.
	 */
	public void applyRandomFastDecay()
	{
		int x, y, z;
		int sectionY;
		int limboHeight;
		World limbo = DimensionManager.getWorld(properties.LimboDimensionID);
		
		if (limbo != null)
		{
			limboHeight = limbo.getHeight();
			
			//Obtain the coordinates of active chunks in Limbo. For each section of each chunk,
			//pick a random block and try to apply fast decay.
			for (Object coordObject : ForgeChunkManager.getPersistentChunksFor(limbo).keySet())
			{
				ChunkCoordIntPair chunkCoord = (ChunkCoordIntPair) coordObject;
				
				//Loop through each chunk section and fast-decay a random block
				//Apply the changes using the world object instead of directly to the chunk so that clients are always notified.
				for (sectionY = 0; sectionY < limboHeight; sectionY += SECTION_HEIGHT)
				{
					x = chunkCoord.chunkXPos * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
					z = chunkCoord.chunkZPos * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
					y = sectionY + random.nextInt(SECTION_HEIGHT);
					decayBlockFast(limbo, x, y, z);
				}
			}
		}
	}
	
	/**
	 * Checks if a block can be decayed and, if so, changes it directly into Unraveled Fabric.
	 */
	private boolean decayBlockFast(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (canDecayBlock(block, world, x, y, z))
		{
			world.setBlock(x, y, z, mod_pocketDim.blockLimbo);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	private boolean decayBlock(World world, int x, int y, int z)
	{
		int index;
		Block block = world.getBlock(x, y, z);
		if (canDecayBlock(block, world, x, y, z))
		{
			//Loop over the block IDs that decay can go through.
			//Find an index matching the current blockID, if any.
			for (index = 0; index < getDecaySequence().length; index++)
			{
				if (getDecaySequence()[index] == block)
				{
					break;
				}
			}
			
			//Since the decay sequence is a reversed list, the block ID in the index before our match
			//is the block ID we should change this block into. A trick in this approach is that if
			//we loop over the array without finding a match, then (index - 1) will contain the
			//last ID in the array, which is the first one that all blocks decay into.
			//We assume that Unraveled Fabric is NOT decayable. Otherwise, this will go out of bounds!
			
			world.setBlock(x, y, z, getDecaySequence()[index - 1]);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a block can decay. We will not decay air, certain DD blocks, or containers.
	 */
	private boolean canDecayBlock(Block block, World world, int x, int y, int z)
	{
		if (block.isAir(world, x, y, z))
		{
			return false;
		}
		
		for (int k = 0; k < getBlocksImmuneToDecay().length; k++)
		{
			if (block == getBlocksImmuneToDecay()[k])
			{
				return false;
			}
		}

		return (block == null || !(block instanceof BlockContainer));
	}
}
