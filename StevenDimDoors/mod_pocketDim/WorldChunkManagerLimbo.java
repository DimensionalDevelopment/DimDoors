package StevenDimDoors.mod_pocketDim;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;
import static net.minecraft.world.biome.BiomeGenBase.*;

public class WorldChunkManagerLimbo extends WorldChunkManager


{
	
	BiomeGenBase biomeGenerator = mod_pocketDim.limboBiome;
	 public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	    {
	       return new BiomeGenBase[] {mod_pocketDim.limboBiome};
	    }
	 
	 public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5, boolean par6)
	    {
		  return new BiomeGenBase[] {mod_pocketDim.limboBiome};
	        
	    }
	 
	 public BiomeGenBase getBiomeGenAt(int par1, int par2)
	    {
		  return  mod_pocketDim.limboBiome;
	    }
	 
	 public boolean areBiomesViable(int par1, int par2, int par3, List par4List)
	    {
	        return par4List.contains(this.biomeGenerator);
	    }



}