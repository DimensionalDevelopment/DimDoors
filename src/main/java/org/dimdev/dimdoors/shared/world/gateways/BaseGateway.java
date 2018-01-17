package org.dimdev.dimdoors.shared.world.gateways;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class BaseGateway {
    public BaseGateway() {}

    /**
     * Generates the gateway centered on the given coordinates
     * @param world - the world in which to generate the gateway
     * @param x - the x-coordinate at which to center the gateway; usually where the door is placed
     * @param y - the y-coordinate of the block on which the gateway may be built
     * @param z - the z-coordinate at which to center the gateway; usually where the door is placed
     */
    public abstract void generate(World world, int x, int y, int z);

    /**
     * Determines whether the specified biome is a valid biome in which to generate this gateway
     * @param biome - the biome to be checked
     * @return <code>true</code> true if the specified biome is a valid for generating this gateway, otherwise <code>false</code>
     */
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

    /**
     * Determines whether the specified world and coordinates are a valid location for generating this gateway
     * @param world - the world in which to generate the gateway
     * @param x - the x-coordinate at which to center the gateway; usually where the door is placed
     * @param y - the y-coordinate of the block on which the gateway may be built
     * @param z - the z-coordinate at which to center the gateway; usually where the door is placed
     * @return <code>true</code> if the location is valid, otherwise <code>false</code>
     */
    public boolean isLocationValid(World world, int x, int y, int z)
    {
        return isBiomeValid(world.getBiome(new BlockPos(x,y,z)));
    }

    /**
     * Gets the lowercase keywords to be used in checking whether a given biome is a valid location for this gateway
     * @return an array of biome keywords to match against
     */
    public Biome[] getBiomes() {
        return null;
    }
}