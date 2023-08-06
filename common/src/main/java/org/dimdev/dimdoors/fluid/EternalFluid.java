package org.dimdev.dimdoors.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public abstract class EternalFluid extends FlowingFluid {


	public static interface EternalFluidDetails {
	//Current Methods - TODO: Upgrade architectury's api to make not needed anymore.
		default void randomTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource randomSource) {}

		@Environment(EnvType.CLIENT)
		default void animateTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource randomSource) {}

		default void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {}

		//TODO: Submit PR to architectury to use LevelReader
		default int getDropOff(LevelReader levelReader) {
			return levelReader.dimensionType().ultraWarm() ? 4 : 2;
		}

		//TODO: Submit PR to architectury to use LevelReader
		default int getSlopeFindDistance(LevelReader levelReader) {
			return levelReader.dimensionType().ultraWarm() ? 1 : 2;
		}

		//TODO: Submit PR to architectury to use LevelReader
		default int getTickDelay(LevelReader levelReader) {
			return levelReader.dimensionType().ultraWarm() ? 10 : 30;
		}

		default int getSpreadDelay(Level level, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
			int tickDelay = this.getTickDelay(level);

			if (!fluidState.isEmpty() && !fluidState2.isEmpty() && !fluidState.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, blockPos) > fluidState.getHeight(level, blockPos) && level.getRandom().nextInt(4) != 0) {
				tickDelay *= 4;
			}

			return tickDelay;
		}

		default void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
			if (direction == Direction.DOWN) {
				if (levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)) {
					if (blockState.getBlock() instanceof LiquidBlock) {
						levelAccessor.setBlock(blockPos, ModBlocks.BLACK_ANCIENT_FABRIC.get().defaultBlockState(), 3);
					}

					return;
				}
			}

			self().spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
		}

		EternalFluidDetails self();

		default boolean isRandomlyTicking() {
			return true;
		}
	}
}
