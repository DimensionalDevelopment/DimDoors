package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

class MaskEchoBlockGoal extends Goal {
    private final MaskEntity mask;

    @Nullable
    private BlockPos blockTarget;

    MaskEchoBlockGoal(MaskEntity mask) {
        this.mask = mask;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return hasValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return hasValidTarget();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (blockTarget == null) {
            return;
        }

        Vec3 target = Vec3.atCenterOf(blockTarget);

        if (mask.distanceToSqr(target) <= 1.4) {
            destroyTarget(blockTarget);
            blockTarget = null;
            return;
        }

        MaskMovement.moveToward(mask, target, MaskConstants.PASSIVE_SPEED * 1.15);
        MaskMovement.breakBlockToward(mask, target, 0.9);
    }

    boolean canTarget(BlockPos pos) {
        return mask.getMaskType() == MaskType.ECHO
                && mask.getMode().isPassive()
                && !mask.isFrozen()
                && !mask.level().getBlockState(pos).isAir()
                && MaskDetection.isBlockInSight(mask, pos, MaskType.ECHO);
    }

    void setTarget(BlockPos pos) {
        if (blockTarget == null
                || mask.distanceToSqr(Vec3.atCenterOf(pos)) < mask.distanceToSqr(Vec3.atCenterOf(blockTarget))) {
            blockTarget = pos.immutable();
        }
    }

    void clearTarget() {
        blockTarget = null;
    }

    private boolean hasValidTarget() {
        if (blockTarget == null) {
            return false;
        }

        if (!canTarget(blockTarget)) {
            blockTarget = null;
            return false;
        }

        return true;
    }

    private void destroyTarget(BlockPos pos) {
        BlockState state = mask.level().getBlockState(pos);

        if (!state.isAir() && state.getDestroySpeed(mask.level(), pos) >= 0.0F) {
            mask.level().destroyBlock(pos, true, mask);

            if (mask.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        18,
                        0.35,
                        0.35,
                        0.35,
                        0.08
                );
            }
        }
    }
}
