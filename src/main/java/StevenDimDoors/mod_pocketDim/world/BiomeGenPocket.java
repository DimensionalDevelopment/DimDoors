package StevenDimDoors.mod_pocketDim.world;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenPocket extends BiomeGenBase
{
	public BiomeGenPocket(int par1)
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
     //   this.spawnableMonsterList.add(new SpawnListEntry(MobObelisk.class, 1, 1, 1));
     //   this.spawnableCreatureList.add(new SpawnListEntry(MobObelisk.class, 1, 1, 1));
//
     //   this.spawnableCaveCreatureList.add(new SpawnListEntry(MobObelisk.class, 1, 1, 1));


    }
}
