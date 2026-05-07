package org.dimdev.dimdoors.util;

import net.minecraft.core.Rotations;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class RotationUtil {
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    private RotationUtil() {
    }

    public static Vec3 directionFromRot(Rotations rot) {
        return directionFromRot(rot.getX(), rot.getY());
    }

    public static Vec3 directionFromRot(float pitch, float yaw) {
        double pitchRad = pitch * DEG_TO_RAD;
        double yawRad = yaw * DEG_TO_RAD;
        double cosPitch = Math.cos(pitchRad);

        return new Vec3(
                (float) (-Math.sin(yawRad) * cosPitch),
                (float) -Math.sin(pitchRad),
                (float) (Math.cos(yawRad) * cosPitch)
        );
    }

    public static Rotations rotFromDirection(Vec3 direction, float roll) {
        if (direction.lengthSqr() < 1.0E-12) {
            return new Rotations(0.0F, 0.0F, roll);
        }

        Vec3 d = direction.normalize();
        double horizontal = Math.sqrt(d.x * d.x + d.z * d.z);

        float pitch = (float) (Math.atan2(-d.y, horizontal) * RAD_TO_DEG);
        float yaw = (float) (Math.atan2(-d.x, d.z) * RAD_TO_DEG);

        return new Rotations(
                Mth.clamp(pitch, -90.0F, 90.0F),
                Mth.wrapDegrees(yaw),
                roll
        );
    }

    public static Rotations rotFromDirection(Vec3 direction, Rotations original) {
        return rotFromDirection(direction, original.getZ());
    }
}