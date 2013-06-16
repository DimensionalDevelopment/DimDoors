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
	//TODO: Stop the madness here...
	//Based on reviewing the code in this mod, I believe that all IWorldGenerators
	//must work as singletons. Given that each call that comes into them is independent,
	//we shouldn't have ANY fields in here! At best they will end up causing bugs.
	//In particular, using our own instance of Random instead of one derived from the world
	//seed means that our chunk generation isn't  linked to the world seed. Bad, very bad!
	//I'm going to fix this later. <_<;; ~SenseiKiwi

	private int minableBlockId;
	private int numberOfBlocks;
	int cycles=40;
	boolean shouldSave = false;
	int count = 0;
	int i;
	int k;
	int j;
	Random rand = new Random();
	boolean shouldGenHere = true;
	LinkData link;
	DimData dimData;

	public static final int MAX_GATEWAY_GENERATION_CHANCE = 10000;
	public static final int MAX_CLUSTER_GENERATION_CHANCE = 10000;
	private static final int CLUSTER_GROWTH_CHANCE = 80;
	private static final int MAX_CLUSTER_GROWTH_CHANCE = 100;
	private static DDProperties properties = null;

	public RiftGenerator()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		//TODO: This code could really use some cleaning up... ~SenseiKiwi

		shouldGenHere = properties.WorldRiftGenerationEnabled && !(world.provider instanceof pocketProvider) && !world.isRemote;

		if (shouldGenHere && random.nextInt(MAX_CLUSTER_GENERATION_CHANCE) < properties.ClusterGenerationChance)
		{
			i = chunkX * 16 - random.nextInt(16);
			k = chunkZ * 16 - random.nextInt(16);
			j = world.getHeightValue(i, k);

			if (j > 20 && world.getBlockId(i, j, k) == 0)
			{
				//	System.out.println(String.valueOf(i)+"x "+String.valueOf(j)+"y "+String.valueOf(k)+"z"+"Large gen");

				link = new LinkData(world.provider.dimensionId, 0,  i, j+1, k, i, j+1, k, true,rand.nextInt(4));
				link = dimHelper.instance.createPocket(link,true, true);
				this.shouldSave=true;

				//	SchematicLoader loader = new SchematicLoader();
				//	loader.init(link);
				//	loader.generateSchematic(link);
				count = 0;
				while (random.nextInt(MAX_CLUSTER_GROWTH_CHANCE) < CLUSTER_GROWTH_CHANCE)
				{
					i = chunkX * 16 - random.nextInt(16);
					k = chunkZ * 16 - random.nextInt(16);

					j = world.getHeightValue(i, k);

					if (world.isAirBlock(i, j + 1, k))
					{
						link = dimHelper.instance.createLink(link.locDimID,link.destDimID, i, j+1, k,link.destXCoord,link.destYCoord,link.destZCoord);
					}
				}
			}
		}

		if (shouldGenHere && random.nextInt(MAX_GATEWAY_GENERATION_CHANCE) < properties.GatewayGenerationChance)
		{
			//	System.out.println("tryingToGen");
			int blockID = Block.stoneBrick.blockID;
			if (world.provider.dimensionId == properties.LimboDimensionID)
			{
				blockID = properties.LimboBlockID;
			}
			i=chunkX*16-random.nextInt(16);
			k=chunkZ*16-random.nextInt(16);

			j= world.getHeightValue(i, k);
			if(j>20 && world.getBlockId(i, j, k)==0)
			{
				//System.out.println(String.valueOf(i)+"x "+String.valueOf(j)+"y "+String.valueOf(k)+"z"+"small gen");
				count=0; 

				if(world.isAirBlock(i, j+1, k))
				{
					if(world.isBlockOpaqueCube(i, j-2, k)||world.isBlockOpaqueCube(i, j-1, k))
					{
						link = new LinkData(world.provider.dimensionId, 0,  i, j+1, k, i, j+1, k, true,rand.nextInt(4));
						link =dimHelper.instance.createPocket(link,true, true);

						for(int xc=-3;xc<4;xc++)
						{
							for(int zc=-3;zc<4;zc++)
							{
								for(int yc=0;yc<200;yc++)
								{
									if(yc==0&&world.isBlockOpaqueCube(i+xc, j-2,k +zc))
									{

										if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+2)
										{
											world.setBlock(i+xc, j-1+yc, k+zc, blockID);
										}
										else if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+3)

										{
											world.setBlock(i+xc, j-1+yc, k+zc, blockID,2,1);

										}
									}

								}

							}
						}

						ItemRiftBlade.placeDoorBlock(world, i, j+1, k, 0, mod_pocketDim.transientDoor);

						{
							world.setBlock(i, j+1, k-1, blockID,0,1);
							world.setBlock(i, j+1, k+1, blockID,0,1);
							world.setBlock(i, j, k-1, blockID,0,1);
							world.setBlock(i, j, k+1, blockID,0,1);
							world.setBlock(i, j+2, k+1, blockID,3,1);
							world.setBlock(i, j+2, k-1, blockID,3,1);
						}
						this.shouldSave = true;
					}
				}
			}
		}
	}
}
