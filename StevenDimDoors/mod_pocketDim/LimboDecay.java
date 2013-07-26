package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

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
	//Must be initialized later since it requires DDProperties to be initialized (for LimboBlockID).
	private static int[] decaySequence = null;
	
	private static Random random = new Random();
	private static DDProperties properties = null;
	
	private LimboDecay() { }

	/**
	 * Initializes the array containing the reversed sequence of block IDs that blocks cycle through during decay.
	 */
	private static void InitializeDecaySequence()
	{
		if (decaySequence == null)
		{
			if (properties == null)
				properties = DDProperties.instance();
			
			decaySequence = new int[] {
					properties.LimboBlockID,
					Block.gravel.blockID,
					Block.cobblestone.blockID,
					Block.stone.blockID
			};
		}
	}
	
	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void ApplySpreadDecay(World world, int x, int y, int z)
	{
		if (properties == null)
			properties = DDProperties.instance();
		
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (random.nextInt(MAX_DECAY_SPREAD_CHANCE) < DECAY_SPREAD_CHANCE)
		{
			//Apply decay to the blocks above, below, and on all four sides.
			//World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
			DecayBlock(world, x - 1, y, z);
			DecayBlock(world, x + 1, y, z);
			DecayBlock(world, x, y, z - 1);
			DecayBlock(world, x, y, z + 1);
			DecayBlock(world, x, y - 1, z);
			DecayBlock(world, x, y + 1, z);
		}
	}
	
	/**
	 * Picks random blocks from each active chunk in Limbo and, if decay is applicable, converts them directly to Unraveled Fabric.
	 * This decay method is designed to stop players from avoiding Limbo decay by building floating structures.
	 */
	public static void ApplyRandomFastDecay()
	{
		if (properties == null)
			properties = DDProperties.instance();

		int x, y, z;
		int sectionY;
		int limboHeight;
		World limbo = dimHelper.getWorld(properties.LimboDimensionID);
		
		if (limbo != null)
		{
			limboHeight = limbo.getHeight();
			
			//Obtain the coordinates of active chunks in Limbo. For each section of each chunk,
			//pick a random block and try to apply fast decay.
			for (Object coordObject : limbo.activeChunkSet)
			{
				ChunkCoordIntPair chunkCoord = (ChunkCoordIntPair) coordObject;
				
				//Loop through each chunk section and fast-decay a random block
				//Apply the changes using the world object instead of directly to the chunk so that clients are always notified.
				for (sectionY = 0; sectionY < limboHeight; sectionY += SECTION_HEIGHT)
				{
					x = chunkCoord.chunkXPos * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
					z = chunkCoord.chunkZPos * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
					y = sectionY + random.nextInt(SECTION_HEIGHT);
					DecayBlockFast(limbo, x, y, z);
				}
			}
		}
	}
	
	/**
	 * Checks if a block can be decayed and, if so, changes it directly into Unraveled Fabric.
	 */
	private static boolean DecayBlockFast(World world, int x, int y, int z)
	{
		int blockID = world.getBlockId(x, y, z);
		if (CanDecayBlock(blockID))
		{
			world.setBlock(x, y, z, properties.LimboBlockID);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	private static boolean DecayBlock(World world, int x, int y, int z)
	{
		//Make sure the decay sequence is initialized
		InitializeDecaySequence();
		
		int index;
		int blockID = world.getBlockId(x, y, z);
		if (CanDecayBlock(blockID))
		{
			//Loop over the block IDs that decay can go through.
			//Find an index matching the current blockID, if any.
			for (index = 0; index < decaySequence.length; index++)
			{
				if (decaySequence[index] == blockID)
				{
					break;
				}
			}
			
			//Since the decay sequence is a reversed list, the block ID in the index before our match
			//is the block ID we should change this block into. A trick in this approach is that if
			//we loop over the array without finding a match, then (index - 1) will contain the
			//last ID in the array, which is the first one that all blocks decay into.
			//We assume that Unraveled Fabric is NOT decayable. Otherwise, this will go out of bounds!
			
			world.setBlock(x, y, z, decaySequence[index - 1]);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a block can decay. We will not decay air, Unraveled Fabric, Eternal Fabric, or containers.
	 */
	private static boolean CanDecayBlock(int blockID)
	{
		if (blockID == 0 || blockID == properties.LimboBlockID || blockID == properties.PermaFabricBlockID)
			return false;
		
		Block block = Block.blocksList[blockID];
		return (block == null || !(block instanceof BlockContainer));
	}
}
