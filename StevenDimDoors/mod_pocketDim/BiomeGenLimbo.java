package StevenDimDoors.mod_pocketDim;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenLimbo extends BiomeGenBase
{
    protected BiomeGenLimbo(int par1)
    {
        super(par1);
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.setBiomeName("Limbo");
        this.setDisableRain();
        
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();

    }
}
