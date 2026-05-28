package org.dimdev.dimdoors.entity.mask;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

class MaskPassiveScanGoal extends Goal {
    private final MaskEntity mask;
    private int passiveScanTicks;

    MaskPassiveScanGoal(MaskEntity mask) {
        this.mask = mask;
    }

    @Override
    public boolean canUse() {
        return !mask.isFrozen() && mask.getMode().isPassive();
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
        if (++passiveScanTicks < MaskConstants.PASSIVE_SCAN_INTERVAL) {
            return;
        }

        passiveScanTicks = 0;

        if (mask.getMaskType() == MaskType.FORESIGHT && MaskDetection.dodgeProjectile(mask)) {
            Player nearest = mask.findNearestPlayer(MaskConstants.CHASE_RANGE);
            if (nearest != null) {
                MaskAlert.alertPocket(mask, nearest);
            }
            return;
        }

        Player player = MaskDetection.findDetectedPlayer(mask);
        if (player != null) {
            MaskAlert.alertPocket(mask, player);
        }
    }

    @Override
    public void stop() {
        passiveScanTicks = 0;
    }
}
