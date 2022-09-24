package org.dimdev.dimdoors.entity.ai;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.dimdev.dimdoors.entity.MaskEntity;

public class MaskPatrolMove extends MoveToTargetPosGoal {
	public MaskPatrolMove(MaskEntity mob) {
		super(mob, 0.1f, 1000);
	}

	@Override
	public void tick() {
		super.tick();

		if(hasReached()) {
			System.out.println("Current State: " + targetPos);
			targetPos = ((MaskEntity) mob).getPatrolData().getNextTarget();
			System.out.println("Next State: " + targetPos);
		}
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos) {
		return true;
	}

	@Override
	protected boolean findTargetPos() {
		targetPos = ((MaskEntity) mob).getPatrolData().getCurrentTarget();
		return true;
	}

	@Override
	protected BlockPos getTargetPos() {
		return targetPos;
	}
}
