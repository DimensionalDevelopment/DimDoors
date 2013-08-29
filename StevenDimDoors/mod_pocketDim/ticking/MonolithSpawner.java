package StevenDimDoors.mod_pocketDim.ticking;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.util.ChunkLocation;

public class MonolithSpawner implements IRegularTickReceiver {

	public static final int MAX_MONOLITH_SPAWNING_CHANCE = 100;
	private static final String MOB_SPAWNING_RULE = "doMobSpawning";
	private static final int MAX_MONOLITH_SPAWN_Y = 245;
	private static final int CHUNK_SIZE = 16;
	private static final int MONOLITH_SPAWNING_INTERVAL = 1;
	
	private DDProperties properties;
	private ArrayList<ChunkLocation> locations;
	
	public MonolithSpawner(IRegularTickSender sender, DDProperties properties)
	{
		this.properties = properties;
		this.locations = new ArrayList<ChunkLocation>();
		sender.registerForTicking(this, MONOLITH_SPAWNING_INTERVAL, false);
	}
	
	@Override
	public void notifyTick() {
		
		//Check if any new spawning requests have come in
		if (!locations.isEmpty())
		{
			//Check if mob spawning is allowed
			if (isMobSpawningAllowed())
			{
				//Loop over the locations and call the appropriate function depending
				//on whether the request is for Limbo or for a pocket dimension.
				for (ChunkLocation location : locations)
				{
					if (location.DimensionID == properties.LimboDimensionID)
					{
						//Limbo chunk
						placeMonolithsInLimbo(location.DimensionID, location.ChunkX, location.ChunkZ);
					}
					else
					{
						//Pocket dimension chunk
						placeMonolithsInPocket(location.DimensionID, location.ChunkX, location.ChunkZ);
					}
				}
			}
			
			locations.clear();
		}
	}

	public void registerChunkForPopulation(int dimensionID, int chunkX, int chunkZ)
	{
		ChunkLocation location = new ChunkLocation(dimensionID, chunkX, chunkZ);
		locations.add(location);
	}
	
	private void placeMonolithsInPocket(int dimensionID, int chunkX, int chunkZ)
	{
		NewDimData dimension = PocketManager.getDimensionData(dimensionID);
		World pocket = DimensionManager.getWorld(dimensionID);

		if (pocket == null ||
			dimension == null ||
			dimension.dungeon() == null ||
			dimension.dungeon().isOpen())
		{
			return;
		}
		
		int sanity = 0;
		int blockID = 0;
		boolean didSpawn = false;

		//The following initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(pocket.getSeed() ^ 0xA210FE65F20017D6L);
		long factorA = random.nextLong() / 2L * 2L + 1L;
		long factorB = random.nextLong() / 2L * 2L + 1L;
		random.setSeed(chunkX * factorA + chunkZ * factorB ^ pocket.getSeed());

		//The following code really, really needs to be rewritten... "sanity" is not a proper variable name. ~SenseiKiwi
		int x, y, z;
		do
		{
			//Select a random column within the chunk
			x = chunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
			z = chunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
			y = MAX_MONOLITH_SPAWN_Y;
			blockID = pocket.getBlockId(x, y, z);

			while (blockID == 0 &&y>0)
			{
				y--;
				blockID = pocket.getBlockId(x, y, z);

			}
			while ((blockID == properties.FabricBlockID || blockID == properties.PermaFabricBlockID) && y > 0)
			{
				y--;
				blockID = pocket.getBlockId(x, y, z);
			}
			while (blockID == 0 && y > 0)
			{
				y--;
				blockID = pocket.getBlockId(x, y, z);
			}
			if(y > 0)
			{
				int jumpSanity = 0;
				int jumpHeight = 0;
				do
				{
					jumpHeight = y + random.nextInt(10);
					jumpSanity++;
				}
				while (!pocket.isAirBlock(x,jumpHeight+6 , z)&&jumpSanity<20);

				Entity monolith = new MobMonolith(pocket);
				monolith.setLocationAndAngles(x, jumpHeight, z, 1, 1);
				pocket.spawnEntityInWorld(monolith);
				didSpawn = true;
			}
			sanity++;
		}
		while (sanity < 5 && !didSpawn);
	}

	private void placeMonolithsInLimbo(int dimensionID, int chunkX, int chunkZ)
	{
		World limbo = DimensionManager.getWorld(dimensionID);
		
		if (limbo == null)
		{
			return;
		}
		
		//The following initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(limbo.getSeed() ^ 0xB5130C4ACC71A822L);
		long factorA = random.nextLong() / 2L * 2L + 1L;
		long factorB = random.nextLong() / 2L * 2L + 1L;
		random.setSeed(chunkX * factorA + chunkZ * factorB ^ limbo.getSeed());

		//Okay, the following code is full of magic constants and makes little sense. =/ ~SenseiKiwi
		if (random.nextInt(MAX_MONOLITH_SPAWNING_CHANCE) < properties.MonolithSpawningChance)
		{
			int y = 0;
			int yTest;
			do
			{
				int x = chunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
				int z = chunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);

				while (limbo.getBlockId(x, y, z) == 0 && y <255)
				{
					y++;
				}
				y = yCoordHelper.getFirstUncovered(limbo, x, y + 2, z);
				yTest = yCoordHelper.getFirstUncovered(limbo, x, y + 5, z);
				if (yTest > 245)
				{
					return;
				}

				int jumpSanity = 0;
				int jumpHeight = 0;
				do
				{
					jumpHeight = y + random.nextInt(25);
					jumpSanity++;
				}
				while (!limbo.isAirBlock(x, jumpHeight + 6, z) && jumpSanity < 20);


				Entity monolith = new MobMonolith(limbo);
				monolith.setLocationAndAngles(x, jumpHeight, z, 1, 1);
				limbo.spawnEntityInWorld(monolith);
			}
			while (yTest > y);
		}
	}
	
	private static boolean isMobSpawningAllowed()
	{
		//This function is used to retrieve the value of doMobSpawning. The code is the same
		//as the code used by Minecraft. Jaitsu requested this to make testing easier. ~SenseiKiwi
		
		GameRules rules = MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
		return rules.getGameRuleBooleanValue(MOB_SPAWNING_RULE);
	}
}
