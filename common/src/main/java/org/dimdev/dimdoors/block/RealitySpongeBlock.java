package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import org.dimdev.dimdoors.world.decay.LimboDecay;

public class RealitySpongeBlock extends Block {

	public RealitySpongeBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		for (Direction direction : Direction.values()) {
			BlockPos currentPos = pos.offset(direction);

			if(!world.isAir(currentPos)) {
				System.out.println("Decaying.");
				LimboDecay.decayBlock(world, currentPos, state);
			}
		}
	}
}
