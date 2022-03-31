package org.dimdev.dimdoors.world.feature.gateway.schematic;

import net.minecraft.block.Blocks;
import net.minecraft.block.SandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class EndGateway extends SchematicGateway{
	public EndGateway() {
		super("end_gateway");
	}

	@Override
	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.END_STONE);
	}
}
