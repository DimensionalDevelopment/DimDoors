package org.dimdev.dimdoors.entity.ai.mask;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.dimdev.dimdoors.entity.AbstractMaskEntity;

public class MaskPatrolMove extends MoveToTargetPosGoal {
	public MaskPatrolMove(AbstractMaskEntity mob) {
		super(mob, 0.1f, 1000);
	}

	@Override
	public boolean canStart() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if(hasReached()) {

		}
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos) {
		return true;
	}

	@Override
	protected boolean findTargetPos() {
		return true;
	}

	@Override
	protected BlockPos getTargetPos() {
		return targetPos;
	}
}
