package org.dimdev.dimdoors.world.feature.gateway.schematic;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class TwoPillarsGateway extends SchematicGateway {
	private static final int GATEWAY_RADIUS = 4;

	public TwoPillarsGateway() {
		super("two_pillars");
	}

	@Override
	protected void generateRandomBits(StructureWorldAccess world, BlockPos pos) {
		//Replace some of the ground around the gateway with bricks
		for (int xc = -GATEWAY_RADIUS; xc <= GATEWAY_RADIUS; xc++) {
			for (int zc = -GATEWAY_RADIUS; zc <= GATEWAY_RADIUS; zc++) {
				//Check that the block is supported by an opaque block.
				//This prevents us from building over a cliff, on the peak of a mountain,
				//or the surface of the ocean or a frozen lake.
				if (world.getBlockState(pos.add(xc, -1, zc)).getMaterial().isSolid()) {
					//Randomly choose whether to place bricks or not. The math is designed so that the
					//chances of placing a block decrease as we get farther from the gateway's center.
					int i = Math.abs(xc) + Math.abs(zc);
					if (i < world.getRandom().nextInt(2) + 3) {
						//Place Stone Bricks
						world.setBlockState(pos.add(xc, 0, zc), Blocks.STONE_BRICKS.getDefaultState(), 2);
					} else if (i < world.getRandom().nextInt(3) + 3) {
						//Place Cracked Stone Bricks
						world.setBlockState(pos.add(xc, 0, zc), Blocks.CRACKED_STONE_BRICKS.getDefaultState(), 2);
					}
				}
			}
		}
	}

	@Override
	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return true;
	}
}
