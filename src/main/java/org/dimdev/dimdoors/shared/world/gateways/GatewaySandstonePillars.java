package org.dimdev.dimdoors.shared.world.gateways;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class GatewaySandstonePillars extends BaseSchematicGateway {
    public GatewaySandstonePillars() {
        super("sandstonePillars");
    }

    @Override
    public Biome[] getBiomes() {
        return new Biome[] {Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MUTATED_DESERT};
    }
}
