package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

class MaskGuardGoal extends Goal {
    private static final double HOME_CORRECTION_DISTANCE_SQ = 0.45 * 0.45;

    private final MaskEntity mask;
    private int turnCooldown;

    MaskGuardGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mask.canRunMode(MaskMode.GUARD);
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
        Vec3 home = MaskMovement.homeCenter(mask);
        if (mask.distanceToSqr(home) > HOME_CORRECTION_DISTANCE_SQ) {
            MaskMovement.moveTowardWithoutFacing(mask, home, MaskConstants.PASSIVE_SPEED * 0.55);
        } else {
            mask.setDeltaMovement(Vec3.ZERO);
        }

        if (turnCooldown > 0) {
            turnCooldown--;
            return;
        }

        Direction direction = MaskMovement.pickOpenHorizontalDirection(mask);
        if (direction != null) {
            MaskMovement.faceDirection(mask, direction);
        }

        turnCooldown = 45 + mask.getRandom().nextInt(85);
    }

    @Override
    public void stop() {
        turnCooldown = 0;
    }
}
