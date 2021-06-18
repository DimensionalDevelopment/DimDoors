package org.dimdev.dimdoors.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Only works in cases where {@link net.minecraft.block.AbstractBlock#getStateForNeighborUpdate AbstractBlock#getStateForNeighborUpdate} returns an air {@link BlockState}
 */
public interface CustomBreakBlock {
	TypedActionResult<Pair<BlockState, Consumer<BlockEntity>>> customBreakBlock(World world, BlockPos pos, BlockState blockState, Entity breakingEntity);

	/*
	If this causes any issue mixin into FluidState instead.
	Also remember to remove the access wideners.
	 */
	class HackyFluidState extends FluidState {
		private final BlockState blockState;
		private final Consumer<BlockEntity> blockEntityConsumer;

		public HackyFluidState(BlockState blockState, Consumer<BlockEntity> blockEntityConsumer) {
			super(blockState.getFluidState().getFluid(), blockState.getFluidState().getEntries(), blockState.getFluidState().codec);
			this.blockState = blockState;
			this.blockEntityConsumer = blockEntityConsumer;
		}

		@Override
		public BlockState getBlockState() {
			return blockState;
		}

		public Consumer<BlockEntity> getBlockEntityConsumer() {
			return blockEntityConsumer;
		}
	}
}
