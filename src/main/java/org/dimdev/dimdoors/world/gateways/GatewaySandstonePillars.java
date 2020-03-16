package org.dimdev.dimdoors.world.gateways;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class GatewaySandstonePillars extends BaseSchematicGateway {
    public GatewaySandstonePillars() {
        super("sandstone_pillars");
    }

    @Override
    public Biome[] getBiomes() {
        return new Biome[]{Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES};
    }
}
