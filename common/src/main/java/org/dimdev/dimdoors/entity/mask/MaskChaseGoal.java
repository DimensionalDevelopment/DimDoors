package org.dimdev.dimdoors.entity.mask;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

class MaskChaseGoal extends Goal {
    private final MaskEntity mask;

    MaskChaseGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mask.canRunMode(MaskMode.CHASE);
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
        Player target = mask.findNearestPlayer(MaskConstants.CHASE_RANGE);

        if (target == null) {
            if (mask.getMaskType() == MaskType.BLACK) {
                mask.discard();
            } else {
                mask.resetToHome();
            }
            return;
        }

        MaskMovement.facePosition(mask, target.position());

        if (mask.distanceToSqr(target) <= MaskConstants.CATCH_DISTANCE_SQ) {
            mask.catchPlayer(target);
            return;
        }

        boolean inSolid = MaskMovement.isInsideSolidBlock(mask);

        MaskMovement.moveToward(
                mask,
                target.getEyePosition(),
                inSolid ? MaskConstants.CHASE_SOLID_SPEED : MaskConstants.CHASE_SPEED
        );

        if (inSolid && mask.tickCount % 5 == 0) {
            MaskMovement.emitDiggingEffects(mask);
        }
    }
}
