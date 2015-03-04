package StevenDimDoors.mod_pocketDim.world;

import net.minecraft.world.biome.BiomeGenBase;

public class DDBiomeGenBase extends BiomeGenBase
{
	public DDBiomeGenBase(int biomeID, String name)
    {
        super(biomeID);
        this.setBiomeName(name);
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.setDisableRain();
        
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }

	public static void checkBiomes(int[] biomes)
	{
		for (int k = 0; k < biomes.length; k++)
		{
			if (getBiomeGenArray()[biomes[k]] != null && !(getBiomeGenArray()[biomes[k]] instanceof DDBiomeGenBase))
			{
				// Crash Minecraft to avoid having people complain to us about strange things
				// that are really the result of silent biome ID conflicts.
				throw new IllegalStateException("There is a biome ID conflict between a biome from Dimensional Doors and another biome type. Fix your configuration!");
			}
		}
	}
}
