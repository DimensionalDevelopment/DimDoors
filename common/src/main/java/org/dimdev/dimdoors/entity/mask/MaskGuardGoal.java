package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

class MaskGuardGoal extends Goal {
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
        MaskMovement.moveToward(mask, MaskMovement.homeCenter(mask), MaskConstants.PASSIVE_SPEED * 0.55);

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
