package org.dimdev.dimdoors.world.feature.gateway.schematic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SandBlock;

public class SandstonePillarsGateway extends SchematicGateway {
    public SandstonePillarsGateway() {
        super("sandstone_pillars");
    }

	@Override
	public boolean test(WorldGenLevel structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.getBlockState(blockPos.above()).getBlock() instanceof SandBlock;
	}
}
