package StevenDimDoors.mod_pocketDim.world;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.world.gateways.BaseGateway;
import StevenDimDoors.mod_pocketDim.world.gateways.GatewayLimbo;
import StevenDimDoors.mod_pocketDim.world.gateways.GatewaySandstonePillars;
import StevenDimDoors.mod_pocketDim.world.gateways.GatewayTwoPillars;
import cpw.mods.fml.common.IWorldGenerator;

public class GatewayGenerator implements IWorldGenerator
{
	public static final int MAX_GATEWAY_GENERATION_CHANCE = 10000;
	public static final int MAX_CLUSTER_GENERATION_CHANCE = 10000;
	private static final int CLUSTER_GROWTH_CHANCE = 80;
	private static final int MAX_CLUSTER_GROWTH_CHANCE = 100;
	private static final int MIN_RIFT_Y = 4;
	private static final int MAX_RIFT_Y = 250;
	private static final int CHUNK_LENGTH = 16;
	private static final int GATEWAY_RADIUS = 4;
	private static final int MAX_GATEWAY_GENERATION_ATTEMPTS = 10;
	private static final int NETHER_CHANCE_CORRECTION = 4;
	private static final int OVERWORLD_DIMENSION_ID = 0;
	private static final int NETHER_DIMENSION_ID = -1;
	private static final int END_DIMENSION_ID = 1;
	
	private static ArrayList<BaseGateway> gateways;
	private static BaseGateway defaultGateway;

	private final DDProperties properties;
	
	public GatewayGenerator(DDProperties properties)
	{
		this.properties = properties;
	
	}
	
	public void initGateways()
	{
		gateways=new ArrayList<BaseGateway>();
		this.defaultGateway=new GatewayTwoPillars(this.properties);
		
		//add gateways here
		gateways.add(new GatewaySandstonePillars(this.properties));
		gateways.add(defaultGateway);
		gateways.add(new GatewayLimbo(this.properties));

	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		//Don't generate rifts or gateways if the rift generation flag is disabled,
		//the current world is a pocket dimension, or the world is remote.
		//Also don't generate anything in The End.
		if (world.isRemote || (!properties.WorldRiftGenerationEnabled) ||
			(world.provider instanceof PocketProvider) || (world.provider.dimensionId == END_DIMENSION_ID)||(world.provider.dimensionId == NETHER_DIMENSION_ID))
		{
			return;
		}
		//This check prevents a crash related to superflat worlds not loading World 0
		if (DimensionManager.getWorld(OVERWORLD_DIMENSION_ID) == null)
		{
			return;
		}

		int x, y, z;
		int attempts;
		boolean valid;
		DimLink link;
		NewDimData dimension;

		//Randomly decide whether to place a cluster of rifts here
		if (random.nextInt(MAX_CLUSTER_GENERATION_CHANCE) < properties.ClusterGenerationChance)
		{
			link = null;
			dimension = null;
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
					//Create a link. If this is not the first time, create a child link and connect it to the first link.
					if (link == null)
					{
						dimension = PocketManager.getDimensionData(world);
						link = dimension.createLink(x, y + 1, z, LinkTypes.DUNGEON,0);
					}
					else
					{
						dimension.createChildLink(x, y + 1, z, link);
					}
				}
			}
			//Randomly decide whether to repeat the process and add another rift to the cluster
			while (random.nextInt(MAX_CLUSTER_GROWTH_CHANCE) < CLUSTER_GROWTH_CHANCE);
		}
		
		//Check if generating structures is enabled and randomly decide whether to place a Rift Gateway here.
		//This only happens if a rift cluster was NOT generated.
		else if (random.nextInt(MAX_GATEWAY_GENERATION_CHANCE) < properties.GatewayGenerationChance &&
				isStructureGenerationAllowed())
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
				//TODO I feel like this is slow and should be optimized. We are linear time with total # of generation restrictions
				//Create an array and copy valid gateways into it
				ArrayList<BaseGateway> validGateways = new ArrayList<BaseGateway>();
				for(BaseGateway gateway:gateways)
				{
					if(gateway.isLocationValid(world, x, y, z, world.getBiomeGenForCoords(x, z)))
					{
						validGateways.add(gateway);
					}
				}
				//Add default gateway if we where unable to find a suitable gateway
				if(validGateways.isEmpty())
				{
					validGateways.add(this.defaultGateway);
				}
				//randomly select a gateway from the pool of viable gateways
				validGateways.get(random.nextInt(validGateways.size())).generate(world, x, y, z);
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
	
	private static boolean isStructureGenerationAllowed()
	{
		return DimensionManager.getWorld(OVERWORLD_DIMENSION_ID).getWorldInfo().isMapFeaturesEnabled();
	}
}
