package org.dimdev.dimdoors.block;

import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.api.util.math.MathUtil;

public class EternalFluidBlock extends FluidBlock {
	private static final EntityTarget TARGET = new EscapeTarget(true);

	public EternalFluidBlock(Block.Settings settings) {
		super(ModFluids.ETERNAL_FLUID, settings);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (world.isClient) {
			return;
		}

		try {
			TARGET.receiveEntity(entity, Vec3d.ZERO, MathUtil.entityEulerAngle(entity), entity.getVelocity());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
