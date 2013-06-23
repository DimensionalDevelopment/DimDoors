package StevenDimDoors.mod_pocketDim.world;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;

public class PocketGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	private World worldObj;

	private static final int MAX_MONOLITH_SPAWN_Y = 245;
	private static final int CHUNK_SIZE = 16;
	
	public PocketGenerator(World par1World, long par2, boolean par4) 
	{
		super(par1World, par2, par4);
		this.worldObj = par1World;
	}
	
	@Override
	public void generateTerrain(int par1, int par2, byte[] par3ArrayOfByte)
	{

	}

	public boolean unloadQueuedChunks()
	{
		return true;
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		byte[] var3 = new byte[32768];

		Chunk chunk = new Chunk(worldObj, var3, chunkX, chunkZ);

		return chunk;
	}

	@Override
	public Chunk loadChunk(int var1, int var2) 
	{
		return super.loadChunk(var1, var2);
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) 
	{
		//Check whether we want to populate this chunk with Monoliths.
        DimData dimData = dimHelper.dimList.get(worldObj.provider.dimensionId);
        
        if (dimData == null ||
        	dimData.dungeonGenerator == null ||
        	dimData.dungeonGenerator.isOpen)
        {
        	return;
        }
        
		//The following initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random random = new Random(worldObj.getSeed());
        long factorA = random.nextLong() / 2L * 2L + 1L;
        long factorB = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long)chunkX * factorA + (long)chunkZ * factorB ^ worldObj.getSeed());
	    
		int x, y, z;
		do
		{
			//Select a random column within the chunk
			x = chunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
			z = chunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
			y = 0;
			
			while (worldObj.getBlockId(x, y, z) == 0 && y < 255)
			{
				y++;
			}
			y = yCoordHelper.getFirstUncovered(worldObj,x , y + 2, z);

			if (worldObj.getBlockId(x, y - 1, z) != mod_pocketDim.blockDimWall.blockID)
			{
				y += random.nextInt(4) + 2;
			}
			if (y <= MAX_MONOLITH_SPAWN_Y)
			{
				Entity mob = new MobObelisk(worldObj);
				mob.setLocationAndAngles(x, y, z, 1, 1);
				worldObj.spawnEntityInWorld(mob);
			}
		}
		while (yCoordHelper.getFirstUncovered(worldObj, x , y, z) > y || random.nextBoolean());
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4) 
	{
		DimData data = dimHelper.dimList.get(this.worldObj.provider.dimensionId);
		if (data != null)
		{
			if (data.dungeonGenerator != null)
			{
				if (data.isDimRandomRift && data.isPocket && !data.dungeonGenerator.isOpen)
				{
					return this.worldObj.getBiomeGenForCoords(var2, var3).getSpawnableList(var1);
				}
			}
		}
		return null;
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5)
	{
		return null;
	}
}