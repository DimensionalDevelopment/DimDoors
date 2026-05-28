package org.dimdev.dimdoors.entity.mask;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

class MaskStunnedGoal extends Goal {
    private final MaskEntity mask;
    private int stunTicks;

    MaskStunnedGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mask.canRunMode(MaskMode.STUNNED);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        stunTicks = MaskConstants.STUN_TICKS;
        mask.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        mask.setDeltaMovement(Vec3.ZERO);

        if (--stunTicks > 0) {
            return;
        }

        Player nearest = mask.findNearestPlayer(MaskConstants.CHASE_RANGE);
        if (nearest != null) {
            mask.startChase(nearest);
            MaskAlert.alertPocket(mask, nearest);
        } else {
            mask.setMode(MaskMode.CHASE);
        }
    }

    @Override
    public void stop() {
        stunTicks = 0;
    }
}
