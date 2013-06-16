package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.world.pocketProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class RiftGenerator implements IWorldGenerator
{
	public static final int MAX_GATEWAY_GENERATION_CHANCE = 10000;
	public static final int MAX_CLUSTER_GENERATION_CHANCE = 10000;
	private static final int CLUSTER_GROWTH_CHANCE = 80;
	private static final int MAX_CLUSTER_GROWTH_CHANCE = 100;
	private static final int MIN_RIFT_Y = 21;
	private static final int MAX_RIFT_Y = 250;
	private static final int CHUNK_LENGTH = 16;
	private static final int GATEWAY_RADIUS = 3;
	private static DDProperties properties = null;

	public RiftGenerator()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		//Don't generate rifts or gateways if the rift generation flag is disabled,
		//the current world is a pocket dimension, or the world is remote.
		if (!properties.WorldRiftGenerationEnabled || world.provider instanceof pocketProvider || world.isRemote)
		{
			return;
		}

		int x, y, z;
		int blockID;
		LinkData link;

		//Randomly decide whether to place a cluster of rifts here
		if (random.nextInt(MAX_CLUSTER_GENERATION_CHANCE) < properties.ClusterGenerationChance)
		{
			link = null;
			do
			{
				//Pick a random point on the surface of the chunk
				x = chunkX * CHUNK_LENGTH - random.nextInt(CHUNK_LENGTH);
				z = chunkZ * CHUNK_LENGTH - random.nextInt(CHUNK_LENGTH);
				y = world.getHeightValue(x, z);

				//If the point is within the acceptable altitude range and the block above is empty, then place a rift
				if (y >= MIN_RIFT_Y && y <= MAX_RIFT_Y && world.isAirBlock(x, y + 1, z))
				{
					//Create a link. If this is the first time, create a dungeon pocket and create a two-way link.
					//Otherwise, create a one-way link and connect to the destination of the first link.
					if (link == null)
					{
						link = new LinkData(world.provider.dimensionId, 0,  x, y + 1, z, x, y + 1, z, true, random.nextInt(4));
						link = dimHelper.instance.createPocket(link, true, true);
					}
					else
					{
						link = dimHelper.instance.createLink(link.locDimID, link.destDimID, x, y + 1, z, link.destXCoord, link.destYCoord, link.destZCoord);
					}
				}
			}
			//Randomly decide whether to repeat the process and add another rift to the cluster
			while (random.nextInt(MAX_CLUSTER_GROWTH_CHANCE) < CLUSTER_GROWTH_CHANCE);
		}

		//Randomly decide whether to place a Rift Gateway here.
		//This only happens if a rift cluster was NOT generated.
		else if (random.nextInt(MAX_GATEWAY_GENERATION_CHANCE) < properties.GatewayGenerationChance)
		{
			//Pick a random point on the surface of the chunk
			x = chunkX * CHUNK_LENGTH - random.nextInt(CHUNK_LENGTH);
			z = chunkZ * CHUNK_LENGTH - random.nextInt(CHUNK_LENGTH);
			y = world.getHeightValue(x, z);

			//Check if the point is within the acceptable altitude range, the block above that point is empty,
			//and at least one of the two blocks under that point are opaque
			if (y >= MIN_RIFT_Y && y <= MAX_RIFT_Y && world.isAirBlock(x, y + 1, z) &&
					world.isBlockOpaqueCube(x, y - 2, z) || world.isBlockOpaqueCube(x, y - 1, z))
			{
				//Create a two-way link between the upper block of the gateway and a pocket dimension
				//That pocket dimension is where we'll start a dungeon!
				link = new LinkData(world.provider.dimensionId, 0,  x, y + 1, z, x, y + 1, z, true, random.nextInt(4));
				link = dimHelper.instance.createPocket(link, true, true);

				//If the current dimension isn't Limbo, build a Rift Gateway out of Stone Bricks
				if (world.provider.dimensionId != properties.LimboDimensionID)
				{
					blockID = Block.stoneBrick.blockID;
					
					//Replace some of the ground around the gateway with bricks
					for (int xc = -GATEWAY_RADIUS; xc <= GATEWAY_RADIUS; xc++)
					{
						for (int zc= -GATEWAY_RADIUS; zc <= GATEWAY_RADIUS; zc++)
						{
							//Check that the block is supported by an opaque block.
							//This prevents us from building over a cliff, on the peak of a mountain,
							//or the surface of the ocean or a frozen lake.
							if (world.isBlockOpaqueCube(x + xc, y - 2, z + zc))
							{
								//Randomly choose whether to place bricks or not. The math is designed so that the
								//chances of placing a block decrease as we get farther from the gateway's center.
								if (Math.abs(xc) + Math.abs(zc) < random.nextInt(3) + 2)
								{
									//Place Stone Bricks
									world.setBlock(x + xc, y - 1, z + zc, blockID);
								}
								else if (Math.abs(xc) + Math.abs(zc) < random.nextInt(3) + 3)
								{
									//Place Cracked Stone Bricks
									world.setBlock(x + xc, y - 1, z + zc, blockID, 2, 1);
								}
							}
						}
					}
					
					//Use Chiseled Stone Bricks to top off the pillars around the door
					world.setBlock(x, y + 2, z + 1, blockID, 3, 1);
					world.setBlock(x, y + 2, z - 1, blockID, 3, 1);					
				}
				else
				{
					//Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
					//that type, there is no point replacing the ground. Just build the tops of the columns here.
					blockID = properties.LimboBlockID;
					world.setBlock(x, y + 2, z + 1, blockID, 0, 1);
					world.setBlock(x, y + 2, z - 1, blockID, 0, 1);
				}
				
				//Place the shiny transient door into a dungeon
				ItemRiftBlade.placeDoorBlock(world, x, y + 1, z, 0, mod_pocketDim.transientDoor);
				
				//Build the columns around the door
				world.setBlock(x, y + 1, z - 1, blockID, 0, 1);
				world.setBlock(x, y + 1, z + 1, blockID, 0, 1);
				world.setBlock(x, y, z - 1, blockID, 0, 1);
				world.setBlock(x, y, z + 1, blockID, 0, 1);				
			}
		}
	}
}
