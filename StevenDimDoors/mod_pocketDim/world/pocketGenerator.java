package StevenDimDoors.mod_pocketDim.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class pocketGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	 private World worldObj;
	 private Random rand = new Random();

	public pocketGenerator(World par1World, long par2, boolean par4) 
	{
		
		super(par1World, par2, par4);
		// TODO Auto-generated constructor stub
		this.worldObj=par1World;
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
	public Chunk provideChunk(int par1, int par2)
    {
       
		 byte[] var3 = new byte[32768];
        
        Chunk var4 = new Chunk(this.worldObj, var3, par1, par2);
       
        return var4;
    }


	@Override
	public Chunk loadChunk(int var1, int var2) 
	{
		// TODO Auto-generated method stub
		return super.loadChunk(var1, var2);
	}

	@Override
	public void populate(IChunkProvider var1, int var2, int var3) 
	{
		if(dimHelper.dimList.containsKey(worldObj.provider.dimensionId))
		{
			if(dimHelper.dimList.get(worldObj.provider.dimensionId).dungeonGenerator==null)
			{
				return;
			}
			else
			{
				if(dimHelper.dimList.get(worldObj.provider.dimensionId).dungeonGenerator.isOpen)
				{
					return;
				}
			}
		}
		int y =0;
		int x = var2*16 + rand.nextInt(16);
		int z = var3*16 + rand.nextInt(16);
		int yTest;
		do
		{
			
			x = var2*16 + rand.nextInt(32)-8;
			z = var3*16 + rand.nextInt(32)-8;
			
			while(this.worldObj.getBlockId(x, y, z)==0&&y<255)
			{
				y++;
			}
			y = yCoordHelper.getFirstUncovered(this.worldObj,x , y+2, z);
			
			if(this.worldObj.getBlockId(x, y-1, z)!=mod_pocketDim.blockDimWall.blockID)
			{
				y= y+rand.nextInt(4)+2;
			}
			
			if(y>245)
			{
				return;
			}
			
			Entity mob = new MobObelisk(this.worldObj);
			mob.setLocationAndAngles(x, y, z, 1, 1);
			this.worldObj.spawnEntityInWorld(mob);
			
		}
		while( yCoordHelper.getFirstUncovered(this.worldObj,x , y, z)>y);
		
		if(rand.nextBoolean())
		{
			this.populate(var1, var2, var3);
		}
		
		
		
		
		// TODO Auto-generated method stub
		
	}

	
	
	
	

	

	@Override
	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3,
			int var4) 
	{
		DimData data = dimHelper.dimList.get(this.worldObj.provider.dimensionId);
		if(data!=null)
		{
			if(data.dungeonGenerator!=null)
			{
				if(data.isDimRandomRift&&data.isPocket&&!data.dungeonGenerator.isOpen)
				{
					ArrayList list = new ArrayList();
				
					return this.worldObj.getBiomeGenForCoords(var2, var3).getSpawnableList(var1);
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2,
			int var3, int var4, int var5) {
		// TODO Auto-generated method stub
		return null;
	}



	

}