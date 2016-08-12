package com.zixiken.dimdoors.ticking;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.helpers.yCoordHelper;
import com.zixiken.dimdoors.util.ChunkLocation;

public class CustomLimboPopulator implements IRegularTickReceiver {

	public static final int MAX_MONOLITH_SPAWNING_CHANCE = 100;
	private static final String MOB_SPAWNING_RULE = "doMobSpawning";
	private static final int MAX_MONOLITH_SPAWN_Y = 245;
	private static final int CHUNK_SIZE = 16;
	private static final int MONOLITH_SPAWNING_INTERVAL = 1;
	
	private DDProperties properties;
	private ConcurrentLinkedQueue<ChunkLocation> locations;
	
	public CustomLimboPopulator(IRegularTickSender sender, DDProperties properties) {
		this.properties = properties;
		this.locations = new ConcurrentLinkedQueue<ChunkLocation>();
		sender.registerReceiver(this, MONOLITH_SPAWNING_INTERVAL, false);
	}
	
	@Override
	public void notifyTick() {
		
		World limboWorld = null;
		
		// Check if any new spawning requests have come in
		if (!locations.isEmpty()) {
			// Check if mob spawning is allowed
			if (isMobSpawningAllowed()) {
				// Loop over the locations and call the appropriate function depending
				// on whether the request is for Limbo or for a pocket dimension.
				for (ChunkLocation location : locations) {
					if (location.DimensionID == properties.LimboDimensionID) {
						// Limbo chunk
						
						// SenseiKiwi: Check if we haven't loaded Limbo for another request in this request
						// cycle. If so, try to load Limbo up. This solves a strange issue with ChickenChunks
						// where CC somehow forces chunks to generate in Limbo if LimboProvider.canRespawnHere()
						// is true, yet when execution reaches this point, Limbo isn't loaded anymore! My theory
						// is that CC force-loads a chunk for some reason, but since there are no players around,
						// Limbo immediately unloads after standard world gen runs, and before this code can run.
						
						if (limboWorld == null) {
							limboWorld = PocketManager.loadDimension(properties.LimboDimensionID);
						}

						placeMonolithsInLimbo(limboWorld, location.ChunkX, location.ChunkZ);
						DimDoors.gatewayGenerator.generate(limboWorld.rand, location.ChunkX, location.ChunkZ,
								limboWorld, limboWorld.getChunkProvider(), limboWorld.getChunkProvider());
					} else {
						//Pocket dimension chunk
						placeMonolithsInPocket(location.DimensionID, location.ChunkX, location.ChunkZ);
					}
				}
			}
			
			locations.clear();
		}
	}

	public void registerChunkForPopulation(int dimensionID, int chunkX, int chunkZ) {
		ChunkLocation location = new ChunkLocation(dimensionID, chunkX, chunkZ);
		locations.add(location);
	}
	
	private void placeMonolithsInPocket(int dimensionID, int chunkX, int chunkZ) {
		DimData dimension = PocketManager.getDimensionData(dimensionID);
		World pocket = DimensionManager.getWorld(dimensionID);

		if (pocket == null || dimension == null || dimension.dungeon() == null || dimension.dungeon().isOpen()) {
			return;
		}
		
		int sanity = 0;
		Block block = Blocks.air;
		boolean didSpawn = false;

		//The following initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(pocket.getSeed() ^ 0xA210FE65F20017D6L);
		long factorA = random.nextLong() / 2L * 2L + 1L;
		long factorB = random.nextLong() / 2L * 2L + 1L;
		random.setSeed(chunkX * factorA + chunkZ * factorB ^ pocket.getSeed());

		//The following code really, really needs to be rewritten... "sanity" is not a proper variable name. ~SenseiKiwi
		BlockPos pos;
		do {
			//Select a random column within the chunk
			pos = new BlockPos(chunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE), chunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE), MAX_MONOLITH_SPAWN_Y);
			block = pocket.getBlockState(pos).getBlock();

			while (block.isAir(pocket, pos) && pos.getY() > 0) {
				pos = pos.down();
				block = pocket.getBlockState(pos).getBlock();
			}

			while ((block == DimDoors.blockDimWall || block == DimDoors.blockDimWallPerm) && pos.getY() > 0) {
				pos = pos.down();
				block = pocket.getBlockState(pos).getBlock();
			}

			while (block.isAir(pocket, pos) && pos.getY() > 0) {
				pos = pos.down();
				block = pocket.getBlockState(pos).getBlock();
			}

			if(pos.getY() > 0) {
				int jumpSanity = 0;
				int jumpHeight = 0;

				do {
					jumpHeight = pos.getY() + random.nextInt(10);
					jumpSanity++;
				}

				while (!pocket.isAirBlock(new BlockPos(pos.getX(),jumpHeight+6 , pos.getZ()))&&jumpSanity<20);

				MobMonolith monolith = new MobMonolith(pocket);
				monolith.setLocationAndAngles(pos.getX(), jumpHeight-(5-monolith.getRenderSizeModifier()*5), pos.getZ(), 1, 1);
				pocket.spawnEntityInWorld(monolith);
				didSpawn = true;
			}

			sanity++;
		}

		while (sanity < 5 && !didSpawn);
	}

	private void placeMonolithsInLimbo(World limbo, int chunkX, int chunkZ) {
		//The following initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(limbo.getSeed() ^ 0xB5130C4ACC71A822L);
		long factorA = random.nextLong() / 2L * 2L + 1L;
		long factorB = random.nextLong() / 2L * 2L + 1L;
		random.setSeed(chunkX * factorA + chunkZ * factorB ^ limbo.getSeed());

		//Okay, the following code is full of magic constants and makes little sense. =/ ~SenseiKiwi
		if (random.nextInt(MAX_MONOLITH_SPAWNING_CHANCE) < properties.MonolithSpawningChance) {
			BlockPos pos = BlockPos.ORIGIN;
			int yTest;

			do {
				pos = new BlockPos(chunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE), 0, chunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE));

				while (limbo.getBlockState(pos).getBlock().isAir(limbo, pos) && pos.getY() <255) {
					pos = pos.up();
				}

				pos = new BlockPos(pos.getX(), yCoordHelper.getFirstUncovered(limbo, pos.up(2)), pos.getZ());
				yTest = yCoordHelper.getFirstUncovered(limbo, pos.up(5));

				if (yTest > 245) {
					return;
				}

				int jumpSanity = 0;
				int jumpHeight = 0;
				do
				{
					jumpHeight = pos.getY() + random.nextInt(25);
					jumpSanity++;
				}
				while (!limbo.isAirBlock(new BlockPos(pos.getX(), jumpHeight + 6, pos.getZ())) && jumpSanity < 20);


				Entity monolith = new MobMonolith(limbo);
				monolith.setLocationAndAngles(pos.getX(), jumpHeight, pos.getZ(), 1, 1);
				limbo.spawnEntityInWorld(monolith);
			}
			while (yTest > pos.getX());
		}
	}
	
	public static boolean isMobSpawningAllowed() {
		//This function is used to retrieve the value of doMobSpawning. The code is the same
		//as the code used by Minecraft. Jaitsu requested this to make testing easier. ~SenseiKiwi
		
		GameRules rules = MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
		return rules.getBoolean(MOB_SPAWNING_RULE);
	}
}
