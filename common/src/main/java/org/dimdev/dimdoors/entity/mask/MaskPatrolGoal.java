package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

class MaskPatrolGoal extends Goal {
    private static final double ARRIVAL_DISTANCE_SQ = 0.45 * 0.45;

    private final MaskEntity mask;

    MaskPatrolGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mask.canRunMode(MaskMode.PATROL);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        MaskPatrolRoute route = mask.getPatrolRoute();

        if (!route.canPatrol()) {
            mask.setMode(MaskMode.GUARD);
            return;
        }

        BlockPos targetPos = route.currentTarget();
        if (targetPos == null) {
            mask.setMode(MaskMode.GUARD);
            return;
        }

        Vec3 target = MaskMovement.patrolCenter(targetPos);

        if (mask.distanceToSqr(target) <= ARRIVAL_DISTANCE_SQ) {
            mask.setDeltaMovement(Vec3.ZERO);
            route.tickPausedAtTarget();
            return;
        }

        route.resetPause();
        MaskMovement.moveToward(mask, target, MaskConstants.PASSIVE_SPEED);
        MaskMovement.breakContactBlocks(mask);
    }
}
