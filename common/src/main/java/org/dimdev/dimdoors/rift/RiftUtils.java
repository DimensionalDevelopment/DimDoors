package org.dimdev.dimdoors.rift;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.FACING;

public class RiftUtils {
    public static <T extends RiftBlockEntity> T registerFunction(@NotNull T riftBlockEntity) {
        riftBlockEntity.register();
        return riftBlockEntity;
    }

    public static record PortalPlane(Vec3 normal, Vec3 tangentX, Vec3 tangentY, Vec3 origin, double halfWidth, double height) {
        private static final double EPSILON = 1.0E-7D;

        public static PortalPlane ofDoor(BlockState state, BlockPos pos) {
            Vec3 normal = Vec3.atLowerCornerOf(state.getValue(FACING).getOpposite().getNormal());
            Vec3 origin = Vec3.atBottomCenterOf(pos).add(normal.scale(0.31));
            Vec3 tangentY = new Vec3(0, 1, 0);
            Vec3 tangentX = normal.cross(tangentY).normalize();
            return new PortalPlane(normal, tangentX, tangentY, origin, 0.5, 2.0);
        }

        public static PortalPlane ofTrapdoor(BlockState state, BlockPos pos) {
            boolean isTop = state.getValue(TrapDoorBlock.HALF) == Half.TOP;
            Vec3 normal = isTop ? new Vec3(0, -1, 0) : new Vec3(0, 1, 0);
            Vec3 origin = Vec3.atBottomCenterOf(pos).add(0, isTop ? 1 : 0, 0);
            Vec3 tangentX = Vec3.atLowerCornerOf(state.getValue(FACING).getNormal());
            Vec3 tangentY = normal.cross(tangentX).normalize();
            return new PortalPlane(normal, tangentX, tangentY, origin, 0.5, 0.5);
        }

        public boolean isTraversed(Level level, Vec3 previousPos, Vec3 currentPos) {
            double dotCurrent = normal.dot(currentPos.subtract(origin));
            double dotPrevious = normal.dot(previousPos.subtract(origin));

            if (sameSide(dotPrevious, dotCurrent) || (Math.abs(dotCurrent) <= EPSILON && Math.abs(dotPrevious) <= EPSILON)) {
                return false;
            }

            Vec3 positionChange = currentPos.subtract(previousPos);

            if (positionChange.lengthSqr() == 0) {
                return false;
            }

            double denominator = normal.dot(positionChange);
            if (Math.abs(denominator) <= EPSILON) {
                return false;
            }

            double intersectionProgress = normal.dot(origin.subtract(previousPos)) / denominator;
            if (intersectionProgress < -EPSILON || intersectionProgress > 1.0D + EPSILON) {
                return false;
            }

            Vec3 pointOfIntersection = previousPos.add(
                    positionChange.scale(intersectionProgress)
            );

            return containsPoint(pointOfIntersection);
        }

        public Vec3 intersectionOrProjection(Vec3 previousPos, Vec3 currentPos) {
            Vec3 positionChange = currentPos.subtract(previousPos);
            double denominator = normal.dot(positionChange);

            if (Math.abs(denominator) > EPSILON) {
                double intersectionProgress = normal.dot(origin.subtract(previousPos)) / denominator;
                if (intersectionProgress >= -EPSILON && intersectionProgress <= 1.0D + EPSILON) {
                    return previousPos.add(positionChange.scale(clamp(intersectionProgress, 0.0D, 1.0D)));
                }
            }

            return project(currentPos);
        }

        public Vec3 project(Vec3 pos) {
            return pos.subtract(normal.scale(normal.dot(pos.subtract(origin))));
        }

        public boolean isTraversed(Level level, Entity entity, Vec3 previousPos, Vec3 currentPos) {
            if (isTraversed(level, previousPos, currentPos)) {
                return true;
            }

            AABB currentBox = entity.getBoundingBox();
            AABB previousBox = currentBox.move(previousPos.subtract(currentPos));
            return isTraversed(level, previousBox, currentBox, previousPos, currentPos);
        }

        public boolean isTraversed(Level level, AABB previousBox, AABB currentBox, Vec3 previousPos, Vec3 currentPos) {
            if (isTraversed(level, previousPos, currentPos)) {
                return true;
            }

            Vec3 positionChange = currentPos.subtract(previousPos);
            if (positionChange.lengthSqr() == 0 || Math.abs(normal.dot(positionChange)) <= EPSILON) {
                return false;
            }

            double previousMin = minSignedDistance(previousBox);
            double previousMax = maxSignedDistance(previousBox);
            double currentMin = minSignedDistance(currentBox);
            double currentMax = maxSignedDistance(currentBox);

            double progress;
            if (previousMin > EPSILON) {
                if (currentMin > EPSILON) {
                    return false;
                }
                progress = previousMin / (previousMin - currentMin);
            } else if (previousMax < -EPSILON) {
                if (currentMax < -EPSILON) {
                    return false;
                }
                progress = previousMax / (previousMax - currentMax);
            } else {
                return false;
            }

            if (Double.isNaN(progress) || progress < -EPSILON || progress > 1.0D + EPSILON) {
                return false;
            }

            AABB crossingBox = interpolate(previousBox, currentBox, clamp(progress, 0.0D, 1.0D));
            return intersectsPortalRectangle(crossingBox);
        }

        private boolean containsPoint(Vec3 point) {
            Vec3 intersectionRelativeToPortalPlane = point.subtract(origin);
            double relativeIntersectionU = intersectionRelativeToPortalPlane.dot(tangentX);
            double relativeIntersectionV = intersectionRelativeToPortalPlane.dot(tangentY);

            return Math.abs(relativeIntersectionU) <= halfWidth + EPSILON
                    && relativeIntersectionV >= -EPSILON && relativeIntersectionV <= height + EPSILON;
        }

        private boolean intersectsPortalRectangle(AABB box) {
            double minU = Double.POSITIVE_INFINITY;
            double maxU = Double.NEGATIVE_INFINITY;
            double minV = Double.POSITIVE_INFINITY;
            double maxV = Double.NEGATIVE_INFINITY;

            for (Vec3 corner : corners(box)) {
                Vec3 relative = corner.subtract(origin);
                double u = relative.dot(tangentX);
                double v = relative.dot(tangentY);

                minU = Math.min(minU, u);
                maxU = Math.max(maxU, u);
                minV = Math.min(minV, v);
                maxV = Math.max(maxV, v);
            }

            return maxU >= -halfWidth - EPSILON
                    && minU <= halfWidth + EPSILON
                    && maxV >= -EPSILON
                    && minV <= height + EPSILON;
        }

        private double minSignedDistance(AABB box) {
            double min = Double.POSITIVE_INFINITY;
            for (Vec3 corner : corners(box)) {
                min = Math.min(min, normal.dot(corner.subtract(origin)));
            }
            return min;
        }

        private double maxSignedDistance(AABB box) {
            double max = Double.NEGATIVE_INFINITY;
            for (Vec3 corner : corners(box)) {
                max = Math.max(max, normal.dot(corner.subtract(origin)));
            }
            return max;
        }

        private static boolean sameSide(double a, double b) {
            return a > EPSILON && b > EPSILON || a < -EPSILON && b < -EPSILON;
        }

        private static AABB interpolate(AABB previousBox, AABB currentBox, double progress) {
            return new AABB(
                    lerp(previousBox.minX, currentBox.minX, progress),
                    lerp(previousBox.minY, currentBox.minY, progress),
                    lerp(previousBox.minZ, currentBox.minZ, progress),
                    lerp(previousBox.maxX, currentBox.maxX, progress),
                    lerp(previousBox.maxY, currentBox.maxY, progress),
                    lerp(previousBox.maxZ, currentBox.maxZ, progress)
            );
        }

        private static double lerp(double start, double end, double progress) {
            return start + (end - start) * progress;
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        private static Vec3[] corners(AABB box) {
            return new Vec3[] {
                    new Vec3(box.minX, box.minY, box.minZ),
                    new Vec3(box.minX, box.minY, box.maxZ),
                    new Vec3(box.minX, box.maxY, box.minZ),
                    new Vec3(box.minX, box.maxY, box.maxZ),
                    new Vec3(box.maxX, box.minY, box.minZ),
                    new Vec3(box.maxX, box.minY, box.maxZ),
                    new Vec3(box.maxX, box.maxY, box.minZ),
                    new Vec3(box.maxX, box.maxY, box.maxZ)
            };
        }
    }
}
