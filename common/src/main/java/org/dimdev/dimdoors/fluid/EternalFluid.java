package org.dimdev.dimdoors.fluid;

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
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_ETERNAL_FLUID.get();
	}

	@Override
	public FluidState getSource(boolean bl) {
		return ModFluids.ETERNAL_FLUID.get().defaultFluidState();
	}

	@Override
	public Item getBucket() {
		return ModItems.ETERNAL_FLUID_BUCKET;
	}

	@Override
	protected void randomTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource randomSource) {

	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void animateTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource randomSource) {

	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {

	}

	@Override
	protected int getDropOff(LevelReader levelReader) {
		return levelReader.dimensionType().ultraWarm() ? 4 : 2;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		return ModBlocks.ETERNAL_FLUID.defaultBlockState().setValue(LEVEL, getLegacyLevel(fluidState));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == ModFluids.ETERNAL_FLUID || fluid == ModFluids.FLOWING_ETERNAL_FLUID;
	}

	@Override
	protected int getSlopeFindDistance(LevelReader levelReader) {
		return levelReader.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.getHeight(blockGetter, blockPos) >= 0.44444445F && fluid.isSame(Fluids.WATER);
	}

	@Override
	public int getTickDelay(LevelReader levelReader) {
		return levelReader.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	protected int getSpreadDelay(Level level, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		int tickDelay = this.getTickDelay(level);

		if (!fluidState.isEmpty() && !fluidState2.isEmpty() && !fluidState.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, blockPos) > fluidState.getHeight(level, blockPos) && level.getRandom().nextInt(4) != 0) {
			tickDelay *= 4;
		}

		return tickDelay;
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return false;
	}

	@Override
	protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			if (levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)) {
				if (blockState.getBlock() instanceof LiquidBlock) {
					levelAccessor.setBlock(blockPos, ModBlocks.BLACK_ANCIENT_FABRIC.defaultBlockState(), 3);
				}

				return;
			}
		}

		super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
	}

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}


	@Override
	protected float getExplosionResistance() {
		return 100000;
	}

	public static class Flowing extends EternalFluid {

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			builder.add(LEVEL);
		}

		@Override
		public Fluid getSource() {
			return null;
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}
	}

	public static class Still extends EternalFluid {
		@Override
		public Fluid getSource() {
			return ModFluids.ETERNAL_FLUID.get().defaultFluidState().getType();
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}
	}
}
