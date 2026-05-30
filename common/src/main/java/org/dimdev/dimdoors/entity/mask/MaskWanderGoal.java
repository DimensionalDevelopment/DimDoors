package org.dimdev.dimdoors.entity.mask;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

class MaskWanderGoal extends Goal {
    private final MaskEntity mask;

    @Nullable
    private Vec3 wanderTarget;

    MaskWanderGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mask.canRunMode(MaskMode.WANDER);
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
        if (wanderTarget != null && !MaskMovement.canWanderToward(mask, wanderTarget)) {
            wanderTarget = null;
        }

        if (wanderTarget == null || mask.distanceToSqr(wanderTarget) < 1.5 || mask.getRandom().nextInt(120) == 0) {
            wanderTarget = MaskMovement.pickWanderTarget(mask);
        }

        if (wanderTarget != null) {
            MaskMovement.moveToward(mask, wanderTarget, MaskConstants.PASSIVE_SPEED * 0.85);
        }
    }

    @Override
    public void stop() {
        wanderTarget = null;
    }
}
