package org.dimdev.dimdoors.world.feature.gateway.schematic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.SandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class SandstonePillarsV2Gateway extends SchematicV2Gateway {
    public SandstonePillarsV2Gateway() {
        super("sandstone_pillars");
    }

    @Override
    public Set<RegistryKey<Biome>> getBiomes() {
        return ImmutableSet.of(BiomeKeys.DESERT, BiomeKeys.DESERT_LAKES, BiomeKeys.DESERT_HILLS);
    }

	@Override
	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.getBlockState(blockPos.down()).getBlock() instanceof SandBlock;
	}
}
