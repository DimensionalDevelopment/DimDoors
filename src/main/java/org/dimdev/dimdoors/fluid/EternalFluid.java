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
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public abstract class EternalFluid extends FlowingFluid {
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_ETERNAL_FLUID;
	}

	@Override
	public Fluid getSource() {
		return ModFluids.ETERNAL_FLUID;
	}

	@Override
	public Item getBucket() {
		return ModItems.ETERNAL_FLUID_BUCKET;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(Level world, BlockPos blockPos, FluidState fluidState, RandomSource random) {

	}

	@Override
	public void randomTick(Level world, BlockPos blockPos, FluidState fluidState, RandomSource random) {

	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor iWorld, BlockPos blockPos, BlockState blockState) {

	}

	@Override
	public int getSlopeFindDistance(LevelReader worldView) {
		return worldView.dimensionType().ultraWarm() ? 4 : 2;
	}

	@Override
	public BlockState createLegacyBlock(FluidState fluidState) {
		return ModBlocks.ETERNAL_FLUID.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == ModFluids.ETERNAL_FLUID || fluid == ModFluids.FLOWING_ETERNAL_FLUID;
	}

	@Override
	public int getDropOff(LevelReader worldView) {
		return worldView.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.getHeight(blockView, blockPos) >= 0.44444445F && fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(LevelReader worldView) {
		return worldView.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	public int getSpreadDelay(Level world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		int tickDelay = this.getTickDelay(world);

		if (!fluidState.isEmpty() && !fluidState2.isEmpty() && !fluidState.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(world, blockPos) > fluidState.getHeight(world, blockPos) && world.getRandom().nextInt(4) != 0) {
			tickDelay *= 4;
		}

		return tickDelay;
	}

	@Override
	protected boolean canConvertToSource(Level world) {
		return false;
	}

	@Override
	protected void spreadTo(LevelAccessor world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			if (world.getFluidState(pos).is(FluidTags.WATER)) {
				if (blockState.getBlock() instanceof LiquidBlock) {
					world.setBlock(pos, ModBlocks.BLACK_ANCIENT_FABRIC.defaultBlockState(), 3);
				}

				return;
			}
		}

		super.spreadTo(world, pos, blockState, direction, fluidState);
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
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}
	}

	public static class Still extends EternalFluid {
		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}
	}
}
