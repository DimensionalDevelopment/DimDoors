package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

final class MaskDetection {
    private MaskDetection() {
    }

    static Player findDetectedPlayer(MaskEntity mask) {
        Player best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Player player : mask.level().players()) {
            if (!canDetect(mask, player)) {
                continue;
            }

            double distance = mask.distanceToSqr(player);
            if (distance < bestDistance) {
                best = player;
                bestDistance = distance;
            }
        }

        return best;
    }

    static boolean canDetect(MaskEntity mask, Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }

        MaskType type = mask.getMaskType();
        if (type == MaskType.BLACK) {
            return true;
        }

        Vec3 toPlayer = player.getEyePosition().subtract(mask.getEyePosition());
        double distance = toPlayer.length();

        if (type == MaskType.SCULKING) {
            return distance <= type.detectionRange()
                    && (player.getDeltaMovement().horizontalDistanceSqr() > 0.0009 || !player.isShiftKeyDown());
        }

        if (distance > type.detectionRange()) {
            return false;
        }

        if (type == MaskType.ENLIGHTENED) {
            return true;
        }

        Vec3 flat = new Vec3(toPlayer.x, 0.0, toPlayer.z);
        if (flat.lengthSqr() < 1.0E-5) {
            return hasLineTo(mask, player);
        }

        double dot = maskForwardVector(mask).dot(flat.normalize());
        boolean inShape = switch (type) {
            case CYCLOP, RANDOM -> dot >= 0.68;
            case ECHO, FORESIGHT -> dot >= 0.0;
            default -> true;
        };

        return inShape && hasLineTo(mask, player);
    }

    static boolean hasLineTo(MaskEntity mask, Player player) {
        HitResult hit = mask.level().clip(new ClipContext(
                mask.getEyePosition(),
                player.getEyePosition(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mask
        ));

        return hit.getType() != HitResult.Type.BLOCK;
    }

    static boolean isBlockInSight(MaskEntity mask, BlockPos pos, MaskType type) {
        Vec3 toBlock = Vec3.atCenterOf(pos).subtract(mask.getEyePosition());
        double distance = toBlock.length();

        if (distance > type.detectionRange()) {
            return false;
        }

        if (type != MaskType.ENLIGHTENED) {
            Vec3 flat = new Vec3(toBlock.x, 0.0, toBlock.z);
            if (flat.lengthSqr() > 1.0E-5) {
                double dot = maskForwardVector(mask).dot(flat.normalize());

                if (type == MaskType.CYCLOP && dot < 0.68) {
                    return false;
                }

                if ((type == MaskType.ECHO || type == MaskType.FORESIGHT) && dot < 0.0) {
                    return false;
                }
            }
        }

        HitResult hit = mask.level().clip(new ClipContext(
                mask.getEyePosition(),
                Vec3.atCenterOf(pos),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mask
        ));

        return hit.getType() != HitResult.Type.BLOCK || BlockPos.containing(hit.getLocation()).equals(pos);
    }

    static boolean dodgeProjectile(MaskEntity mask) {
        AABB search = mask.getBoundingBox().inflate(3.0);
        List<Projectile> projectiles = mask.level().getEntitiesOfClass(
                Projectile.class,
                search,
                projectile -> projectile.getOwner() instanceof Player
        );

        if (projectiles.isEmpty()) {
            return false;
        }

        Projectile projectile = projectiles.get(0);
        Vec3 away = mask.position().subtract(projectile.position());
        Vec3 dodge = new Vec3(-away.z, 0.0, away.x);

        if (dodge.lengthSqr() < 1.0E-5) {
            dodge = maskForwardVector(mask).cross(new Vec3(0.0, 1.0, 0.0));
        }

        mask.setDeltaMovement(mask.getDeltaMovement().add(dodge.normalize().scale(0.22)));
        return true;
    }

    static void emitDetectionBubble(MaskEntity mask) {
        if (!(mask.level() instanceof ServerLevel serverLevel)
                || !mask.getMode().isPassive()
                || mask.tickCount % MaskConstants.DETECTION_BUBBLE_INTERVAL != 0) {
            return;
        }

        MaskType type = mask.getMaskType();
        double range = type.detectionRange();

        if (range <= 0.0 || type == MaskType.BLACK) {
            return;
        }

        int samples = type == MaskType.ENLIGHTENED || type == MaskType.SCULKING ? 20 : 12;
        double arc = switch (type) {
            case CYCLOP, RANDOM -> Math.toRadians(94.0);
            case ECHO, FORESIGHT -> Math.PI;
            case ENLIGHTENED, SCULKING -> Math.PI * 2.0;
            case BLACK -> 0.0;
        };

        Vec3 forward = maskForwardVector(mask);
        double center = Math.atan2(forward.z, forward.x);
        double start = center - arc * 0.5;

        for (int i = 0; i < samples; i++) {
            double angle = samples == 1 ? center : start + arc * i / (samples - 1);
            Vec3 particle = mask.getEyePosition().add(Math.cos(angle) * range, -0.15, Math.sin(angle) * range);

            serverLevel.sendParticles(
                    type == MaskType.SCULKING ? ParticleTypes.SCULK_SOUL : ParticleTypes.END_ROD,
                    particle.x,
                    particle.y,
                    particle.z,
                    1,
                    0.01,
                    0.01,
                    0.01,
                    0.0
            );
        }
    }

    static Vec3 maskForwardVector(MaskEntity mask) {
        float radians = mask.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(radians), 0.0, Mth.cos(radians)).normalize();
    }
}
