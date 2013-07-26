package StevenDimDoors.mod_pocketDim.world;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.ticking.CommonTickHandler;
import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;

public class PocketGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	private World worldObj;

	private DDProperties properties = null;


	
	public PocketGenerator(World par1World, long par2, boolean par4) 
	{
		super(par1World, par2, par4);
		this.worldObj = par1World;
		
		if (properties  == null)
			properties = DDProperties.instance();
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
			chunk.isTerrainPopulated=true;
			CommonTickHandler.chunksToPopulate.add(new int[] {chunk.worldObj.provider.dimensionId,chunkX,chunkZ});
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