package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
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
	private static final int GATEWAY_RADIUS = 4;
	private static final int MAX_GATEWAY_GENERATION_ATTEMPTS = 10;
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
		if ((!properties.WorldRiftGenerationEnabled && !(world.provider instanceof LimboProvider)) ||
			world.provider instanceof pocketProvider || world.isRemote)
		{
			return;
		}

		int x, y, z;
		int attempts;
		int blockID;
		boolean valid;
		LinkData link;

		//Randomly decide whether to place a cluster of rifts here
		if (random.nextInt(MAX_CLUSTER_GENERATION_CHANCE) < properties.ClusterGenerationChance)
		{
			link = null;
			do
			{
				//Pick a random point on the surface of the chunk
				x = chunkX * CHUNK_LENGTH + random.nextInt(CHUNK_LENGTH);
				z = chunkZ * CHUNK_LENGTH + random.nextInt(CHUNK_LENGTH);
				y = world.getHeightValue(x, z);

				//If the point is within the acceptable altitude range, the block above is empty, and we're
				//not building on bedrock, then generate a rift there
				if (y >= MIN_RIFT_Y && y <= MAX_RIFT_Y && world.isAirBlock(x, y + 1, z) &&
					world.getBlockId(x, y, z) != Block.bedrock.blockID &&	//<-- Stops Nether roof spawning. DO NOT REMOVE!
					world.getBlockId(x, y - 1, z) != Block.bedrock.blockID &&
					world.getBlockId(x, y - 2, z) != Block.bedrock.blockID)
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
			valid = false;
			x = y = z = 0; //Stop the compiler from freaking out
			
			//Check locations for the gateway until we are satisfied or run out of attempts.
			for (attempts = 0; attempts < MAX_GATEWAY_GENERATION_ATTEMPTS && !valid; attempts++)
			{
				//Pick a random point on the surface of the chunk and check its materials
				x = chunkX * CHUNK_LENGTH + random.nextInt(CHUNK_LENGTH);
				z = chunkZ * CHUNK_LENGTH + random.nextInt(CHUNK_LENGTH);
				y = world.getHeightValue(x, z);
				valid = checkGatewayLocation(world, x, y, z);
			}

			//Build the gateway if we found a valid location
			if (valid)
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
								if (Math.abs(xc) + Math.abs(zc) < random.nextInt(2) + 3)
								{
									//Place Stone Bricks
									world.setBlock(x + xc, y - 1, z + zc, blockID, 0, 3);
								}
								else if (Math.abs(xc) + Math.abs(zc) < random.nextInt(3) + 3)
								{
									//Place Cracked Stone Bricks
									world.setBlock(x + xc, y - 1, z + zc, blockID, 2, 3);
								}
							}
						}
					}
					
					//Use Chiseled Stone Bricks to top off the pillars around the door
					world.setBlock(x, y + 2, z + 1, blockID, 3, 3);
					world.setBlock(x, y + 2, z - 1, blockID, 3, 3);					
				}
				else
				{
					//Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
					//that type, there is no point replacing the ground. Just build the tops of the columns here.
					blockID = properties.LimboBlockID;
					world.setBlock(x, y + 2, z + 1, blockID, 0, 3);
					world.setBlock(x, y + 2, z - 1, blockID, 0, 3);
				}
				
				//Place the shiny transient door into a dungeon
				ItemRiftBlade.placeDoorBlock(world, x, y + 1, z, 0, mod_pocketDim.transientDoor);
				
				//Build the columns around the door
				world.setBlock(x, y + 1, z - 1, blockID, 0, 3);
				world.setBlock(x, y + 1, z + 1, blockID, 0, 3);
				world.setBlock(x, y, z - 1, blockID, 0, 3);
				world.setBlock(x, y, z + 1, blockID, 0, 3);				
			}
		}
	}
	
	private static boolean checkGatewayLocation(World world, int x, int y, int z)
	{
		//Check if the point is within the acceptable altitude range, the block above that point is empty,
		//and the block two levels down is opaque and has a reasonable material. Plus that we're not building
		//on top of bedrock.
		return (y >= MIN_RIFT_Y &&
				y <= MAX_RIFT_Y &&
				world.isAirBlock(x, y + 1, z) &&
				world.getBlockId(x, y, z) != Block.bedrock.blockID &&	//<-- Stops Nether roof spawning. DO NOT REMOVE!
				world.getBlockId(x, y - 1, z) != Block.bedrock.blockID &&
				checkFoundationMaterial(world, x, y - 2, z));
	}
	
	private static boolean checkFoundationMaterial(World world, int x, int y, int z)
	{
		//We check the material and opacity to prevent generating gateways on top of trees or houses,
		//or on top of strange things like tall grass, water, slabs, or torches.
		//We also want to avoid generating things on top of the Nether's bedrock!
		Material material = world.getBlockMaterial(x, y, z);
		return (material != Material.leaves && material != Material.wood && material != Material.pumpkin
				&& world.isBlockOpaqueCube(x, y, z) && world.getBlockId(x, y, z) != Block.bedrock.blockID);
	}
}
