package org.dimdev.dimdoors.world.feature.gateway.schematic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;

public class TwoPillarsGateway extends SchematicGateway {
	private static final int GATEWAY_RADIUS = 4;

	public TwoPillarsGateway() {
		super("two_pillars");
	}

	@Override
	protected void generateRandomBits(WorldGenLevel world, BlockPos pos) {
		//Replace some of the ground around the gateway with bricks
		for (int xc = -GATEWAY_RADIUS; xc <= GATEWAY_RADIUS; xc++) {
			for (int zc = -GATEWAY_RADIUS; zc <= GATEWAY_RADIUS; zc++) {
				//Check that the block is supported by an opaque block.
				//This prevents us from building over a cliff, on the peak of a mountain,
				//or the surface of the ocean or a frozen lake.
				if (world.getBlockState(pos.offset(xc, -1, zc)).isSolid()) {
					//Randomly choose whether to place bricks or not. The math is designed so that the
					//chances of placing a block decrease as we get farther from the gateway's center.
					int i = Math.abs(xc) + Math.abs(zc);
					if (i < world.getRandom().nextInt(2) + 3) {
						//Place Stone Bricks
						world.setBlock(pos.offset(xc, 0, zc), Blocks.STONE_BRICKS.defaultBlockState(), 2);
					} else if (i < world.getRandom().nextInt(3) + 3) {
						//Place Cracked Stone Bricks
						world.setBlock(pos.offset(xc, 0, zc), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
					}
				}
			}
		}
	}

	@Override
	public boolean test(WorldGenLevel structureWorldAccess, BlockPos blockPos) {
		return !structureWorldAccess.getBlockState(blockPos).is(Blocks.WATER);
	}
}
