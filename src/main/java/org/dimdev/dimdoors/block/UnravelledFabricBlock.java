package org.dimdev.dimdoors.block;

import java.util.Random;

import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.LimboDecay;

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
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (ModDimensions.isLimboDimension(world)) {
			LimboDecay.applySpreadDecay(world, pos);
		}
	}
}
