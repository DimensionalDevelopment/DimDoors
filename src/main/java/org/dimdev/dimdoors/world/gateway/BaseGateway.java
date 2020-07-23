package org.dimdev.dimdoors.world.gateway;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class BaseGateway {
    public BaseGateway() {}

    public abstract void generate(World world, int x, int y, int z);

    protected boolean isBiomeValid(Biome biome) {
        Biome[] biomes = getBiomes();
        if (biomes != null) {
            for (Biome b : biomes) {
                if (b.equals(biome)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isLocationValid(World world, int x, int y, int z) {
        return isBiomeValid(world.getBiome(new BlockPos(x, y, z)));
    }

    public Biome[] getBiomes() {
        return null;
    }
}
