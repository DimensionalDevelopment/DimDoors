package org.dimdev.dimdoors.block;

import java.util.Random;

import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.limbo.LimboDecay;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.WorldAccess;

public class UnravelledFabricBlock extends Block {
	public static final String ID = "unravelled_fabric";

	public UnravelledFabricBlock(Settings settings) {
		super(settings);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (ModDimensions.isLimboDimension(world)) {
			LimboDecay.applySpreadDecay(world, pos);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.scheduledTick(state, world, pos, random);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (world instanceof ServerWorld) {
			world.getBlockTickScheduler().schedule(pos, this, 10, TickPriority.NORMAL);
		}
		return state;
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}
}
