package org.dimdev.dimdoors.api.block;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Only works in cases where {@link net.minecraft.world.level.block.state.BlockBehaviour#updateShape AbstractBlock#getStateForNeighborUpdate} returns an air {@link BlockState}
 */
public interface CustomBreakBlock {
	InteractionResultHolder<Tuple<BlockState, Consumer<BlockEntity>>> customBreakBlock(Level world, BlockPos pos, BlockState blockState, Entity breakingEntity);

	/*
	If this causes any issue mixin into FluidState instead.
	Also remember to remove the access wideners.
	 */
	class HackyFluidState extends FluidState {
		private final BlockState blockState;
		private final Consumer<BlockEntity> blockEntityConsumer;

		public HackyFluidState(BlockState blockState, Consumer<BlockEntity> blockEntityConsumer) {
			super(blockState.getFluidState().getType(), blockState.getFluidState().getValues(), blockState.getFluidState().propertiesCodec);
			this.blockState = blockState;
			this.blockEntityConsumer = blockEntityConsumer;
		}

		@Override
		public BlockState createLegacyBlock() {
			return blockState;
		}

		public Consumer<BlockEntity> getBlockEntityConsumer() {
			return blockEntityConsumer;
		}
	}
}
