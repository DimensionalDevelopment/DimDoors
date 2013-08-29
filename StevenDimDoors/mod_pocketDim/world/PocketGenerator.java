package StevenDimDoors.mod_pocketDim.world;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.ticking.MonolithSpawner;

public class PocketGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	private World worldObj;

	private MonolithSpawner spawner;
	
	public PocketGenerator(World par1World, long par2, boolean par4, MonolithSpawner spawner) 
	{
		super(par1World, par2, par4);
		this.worldObj = par1World;
		
		this.spawner = spawner;
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
		
		if(!chunk.isTerrainPopulated)
		{
			chunk.isTerrainPopulated = true;
			spawner.registerChunkForPopulation(worldObj.provider.dimensionId, chunkX, chunkZ);
		}

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
        
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4) 
	{
		NewDimData dimension = PocketManager.getDimensionData(this.worldObj);
		if (dimension != null && dimension.dungeon() != null && !dimension.dungeon().isOpen())
		{
			return this.worldObj.getBiomeGenForCoords(var2, var3).getSpawnableList(var1);
		}
		return null;
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5)
	{
		return null;
	}
}