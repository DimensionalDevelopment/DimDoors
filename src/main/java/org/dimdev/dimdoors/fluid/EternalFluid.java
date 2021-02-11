package org.dimdev.dimdoors.fluid;

import java.util.Random;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class EternalFluid extends FlowableFluid {
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_ETERNAL_FLUID;
	}

	@Override
	public Fluid getStill() {
		return ModFluids.ETERNAL_FLUID;
	}

	@Override
	public Item getBucketItem() {
		return ModItems.ETERNAL_FLUID_BUCKET;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {

	}

	@Override
	public void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {

	}

	@Override
	protected void beforeBreakingBlock(WorldAccess iWorld, BlockPos blockPos, BlockState blockState) {

	}

	@Override
	public int getFlowSpeed(WorldView worldView) {
		return worldView.getDimension().isUltrawarm() ? 4 : 2;
	}

	@Override
	public BlockState toBlockState(FluidState fluidState) {
		return ModBlocks.ETERNAL_FLUID.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(fluidState));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == ModFluids.ETERNAL_FLUID || fluid == ModFluids.FLOWING_ETERNAL_FLUID;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView worldView) {
		return worldView.getDimension().isUltrawarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.getHeight(blockView, blockPos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return worldView.getDimension().isUltrawarm() ? 10 : 30;
	}

	@Override
	public int getNextTickDelay(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		int tickDelay = this.getTickRate(world);

		if (!fluidState.isEmpty() && !fluidState2.isEmpty() && !fluidState.get(FALLING) && !fluidState2.get(FALLING) && fluidState2.getHeight(world, blockPos) > fluidState.getHeight(world, blockPos) && world.getRandom().nextInt(4) != 0) {
			tickDelay *= 4;
		}

		return tickDelay;
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected void flow(WorldAccess world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
				if (blockState.getBlock() instanceof FluidBlock) {
					world.setBlockState(pos, ModBlocks.BLACK_ANCIENT_FABRIC.getDefaultState(), 3);
				}

				return;
			}
		}

		super.flow(world, pos, blockState, direction, fluidState);
	}

	@Override
	protected boolean hasRandomTicks() {
		return true;
	}

	@Override
	protected float getBlastResistance() {
		return 100000;
	}

	public static class Flowing extends EternalFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState fluidState) {
			return fluidState.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
	}

	public static class Still extends EternalFluid {
		@Override
		public int getLevel(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
	}
}
