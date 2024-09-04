//package org.dimdev.dimdoors.world.feature.gateway.schematic;
//
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.block.Blocks;
//
//public class EndGateway extends SchematicGateway{
//	public EndGateway() {
//		super("end_gateway");
//	}
//
//	@Override
//	public boolean test(WorldGenLevel structureWorldAccess, BlockPos blockPos) {
//		return structureWorldAccess.getBlockState(blockPos.below()).is(Blocks.END_STONE);
//	}
//}
