package org.dimdev.dimdoors.world.feature.gateway.schematic;

import net.minecraft.block.SandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class SandstonePillarsGateway extends SchematicGateway {
    public SandstonePillarsGateway() {
        super("sandstone_pillars");
    }

	@Override
	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.getBlockState(blockPos.down()).getBlock() instanceof SandBlock;
	}
}
