package StevenDimDoors.mod_pocketDim;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class pocketGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	 private World world;

	public pocketGenerator(World par1World, long par2, boolean par4) 
	{
		
		super(par1World, par2, par4);
		// TODO Auto-generated constructor stub
		this.world=par1World;
	}
	@Override
	public void generateTerrain(int par1, int par2, byte[] par3ArrayOfByte)
    {
    
    }
	
	

	@Override
	public Chunk provideChunk(int par1, int par2)
    {
       
		 byte[] var3 = new byte[32768];
        
        Chunk var4 = new Chunk(this.world, var3, par1, par2);
       
        return var4;
    }


	@Override
	public Chunk loadChunk(int var1, int var2) {
		// TODO Auto-generated method stub
		return super.loadChunk(var1, var2);
	}

	@Override
	public void populate(IChunkProvider var1, int var2, int var3) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	

	

	@Override
	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3,
			int var4) 
	{
		
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