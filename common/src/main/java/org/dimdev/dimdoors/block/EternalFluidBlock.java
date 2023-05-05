package org.dimdev.dimdoors.block;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.entity.limbo.LimboExitReason;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;

public class EternalFluidBlock extends ArchitecturyFlowingFluid {
	private static final EntityTarget TARGET = new EscapeTarget(true);

	public EternalFluidBlock() {
		super(ModFluids.ETERNAL_FLUID);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (world.isClient) {
			return;
		}

		try {
			if (TARGET.receiveEntity(entity, Vec3d.ZERO, MathUtil.entityEulerAngle(entity), entity.getVelocity())) {
				if (entity instanceof PlayerEntity) {
					LimboExitReason.ETERNAL_FLUID.broadcast((PlayerEntity) entity);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
