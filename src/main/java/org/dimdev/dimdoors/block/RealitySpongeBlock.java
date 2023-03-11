package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.world.decay.LimboDecay;

public class RealitySpongeBlock extends Block {

	public RealitySpongeBlock(Properties settings) {
		super(settings);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		for (Direction direction : Direction.values()) {
			BlockPos currentPos = pos.relative(direction);

			if(!world.isEmptyBlock(currentPos)) {
				System.out.println("Decaying.");
				LimboDecay.decayBlock(world, currentPos, state);
			}
		}
	}
}
