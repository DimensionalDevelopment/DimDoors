package StevenDimDoors.mod_pocketDim;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenPocket extends BiomeGenBase
{
    protected BiomeGenPocket(int par1)
    {
        super(par1);
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.setBiomeName("Pocket Dimension");
        this.setDisableRain();
        
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }
}
