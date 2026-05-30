package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

final class MaskMovement {
    private MaskMovement() {
    }

    static void travel(MaskEntity mask, Vec3 travelVector) {
        if (mask.isFrozen()) {
            mask.setDeltaMovement(Vec3.ZERO);
            return;
        }

        mask.move(MoverType.SELF, mask.getDeltaMovement());
        mask.setDeltaMovement(mask.getDeltaMovement().scale(mask.getMode() == MaskMode.CHASE ? 0.86 : 0.72));
        mask.calculateEntityAnimation(false);
    }

    static void moveToward(MaskEntity mask, Vec3 target, double speed) {
        if (addMovementToward(mask, target, speed)) {
            facePosition(mask, target);
        }
    }

    static void moveTowardWithoutFacing(MaskEntity mask, Vec3 target, double speed) {
        addMovementToward(mask, target, speed);
    }

    private static boolean addMovementToward(MaskEntity mask, Vec3 target, double speed) {
        Vec3 delta = target.subtract(mask.position());
        if (delta.lengthSqr() < 1.0E-5) {
            return false;
        }

        Vec3 desired = delta.normalize().scale(speed);
        mask.setDeltaMovement(mask.getDeltaMovement().scale(0.65).add(desired.scale(0.35)));
        return true;
    }

    static Vec3 homeCenter(MaskEntity mask) {
        BlockPos home = mask.getHomePos() == null ? mask.blockPosition() : mask.getHomePos();
        return patrolCenter(home);
    }

    static Vec3 patrolCenter(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5);
    }

    static void breakContactBlocks(MaskEntity mask) {
        if (mask.tickCount % 4 != 0) {
            return;
        }

        Vec3 movement = mask.getDeltaMovement();
        AABB box = mask.getBoundingBox().move(movement).inflate(0.05);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = mask.level().getBlockState(pos);
            if (canBreakContactBlock(mask, pos, state)) {
                destroyBreakableBlock(mask, pos, state);
                return;
            }
        }
    }

    private static boolean canBreakContactBlock(MaskEntity mask, BlockPos pos, BlockState state) {
        return !state.isAir()
                && !state.getCollisionShape(mask.level(), pos).isEmpty()
                && state.getDestroySpeed(mask.level(), pos) >= 0.0F;
    }

    private static void destroyBreakableBlock(MaskEntity mask, BlockPos pos, BlockState state) {
        mask.level().destroyBlock(pos, true, mask);
        mask.level().playSound(
                null,
                pos,
                SoundEvents.STONE_BREAK,
                SoundSource.BLOCKS,
                0.9F,
                0.85F + mask.getRandom().nextFloat() * 0.25F
        );

        if (mask.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    14,
                    0.3,
                    0.3,
                    0.3,
                    0.08
            );
        }
    }

    @Nullable
    static Vec3 pickWanderTarget(MaskEntity mask) {
        Vec3 origin = homeCenter(mask);

        for (int i = 0; i < 20; i++) {
            Vec3 candidate = origin.add(
                    (mask.getRandom().nextDouble() - 0.5) * 14.0,
                    (mask.getRandom().nextDouble() - 0.5) * 4.0,
                    (mask.getRandom().nextDouble() - 0.5) * 14.0
            );

            if (canWanderToward(mask, candidate)) {
                return candidate;
            }
        }

        return null;
    }

    static boolean canWanderToward(MaskEntity mask, Vec3 target) {
        return !isTooCloseToWall(mask, target) && hasClearWanderPath(mask, target);
    }

    private static boolean isTooCloseToWall(MaskEntity mask, Vec3 target) {
        AABB targetBox = mask.getBoundingBox().move(target.subtract(mask.position())).inflate(0.08);
        return !mask.level().noCollision(mask, targetBox);
    }

    private static boolean hasClearWanderPath(MaskEntity mask, Vec3 target) {
        HitResult hit = mask.level().clip(new ClipContext(
                mask.position().add(0.0, 0.5, 0.0),
                target.add(0.0, 0.5, 0.0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mask
        ));

        return hit.getType() != HitResult.Type.BLOCK;
    }

    @Nullable
    static Direction pickOpenHorizontalDirection(MaskEntity mask) {
        Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int start = mask.getRandom().nextInt(directions.length);

        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[(start + i) % directions.length];

            if (!wallWithin(mask, direction, 3)) {
                return direction;
            }
        }

        return null;
    }

    static boolean wallWithin(MaskEntity mask, Direction direction, int blocks) {
        BlockPos origin = mask.blockPosition();

        for (int i = 1; i <= blocks; i++) {
            BlockPos pos = origin.relative(direction, i);
            BlockState state = mask.level().getBlockState(pos);

            if (!state.isAir() && state.isSolid()) {
                return true;
            }
        }

        return false;
    }

    static void breakBlockToward(MaskEntity mask, Vec3 target, double probeDistance) {
        if (mask.tickCount % 4 != 0) {
            return;
        }

        Vec3 delta = target.subtract(mask.position());
        if (delta.lengthSqr() < 1.0E-5) {
            return;
        }

        BlockPos pos = BlockPos.containing(mask.position().add(delta.normalize().scale(probeDistance)));
        BlockState state = mask.level().getBlockState(pos);

        if (!state.isAir() && state.getDestroySpeed(mask.level(), pos) >= 0.0F) {
            destroyBreakableBlock(mask, pos, state);
        }
    }

    static boolean isInsideSolidBlock(MaskEntity mask) {
        BlockState body = mask.level().getBlockState(mask.blockPosition());
        BlockState eyes = mask.level().getBlockState(BlockPos.containing(mask.getEyePosition()));

        return (!body.isAir() && body.isSolid()) || (!eyes.isAir() && eyes.isSolid());
    }

    static void emitDiggingEffects(MaskEntity mask) {
        BlockPos pos = mask.blockPosition();
        BlockState state = mask.level().getBlockState(pos);

        if (state.isAir()) {
            return;
        }

        mask.level().playSound(
                null,
                pos,
                SoundEvents.STONE_HIT,
                SoundSource.HOSTILE,
                0.45F,
                0.7F
        );

        if (mask.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    mask.getX(),
                    mask.getY() + 0.5,
                    mask.getZ(),
                    8,
                    0.25,
                    0.25,
                    0.25,
                    0.03
            );
        }
    }

    static void faceDirection(MaskEntity mask, Direction direction) {
        mask.setYRot(direction.toYRot());
        mask.yBodyRot = mask.getYRot();
        mask.yHeadRot = mask.getYRot();
    }

    static void facePosition(MaskEntity mask, Vec3 target) {
        Vec3 delta = target.subtract(mask.position());

        if (delta.horizontalDistanceSqr() < 1.0E-5) {
            return;
        }

        float yaw = (float) (Mth.atan2(delta.z, delta.x) * Mth.RAD_TO_DEG) - 90.0F;
        mask.setYRot(yaw);
        mask.yBodyRot = yaw;
        mask.yHeadRot = yaw;
    }
}
