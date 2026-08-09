package org.dimdev.dimdoors.rift;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.util.Timer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.FACING;

public class RiftUtils {
    private static final double RIFT_CORE_FADE_TICKS = 40;
    private static final Timer.MutableTickSource RIFT_CORE_TICK_SOURCE = Timer.mutableTickSource();

    public static final Timer showRiftCoreUntil = new Timer(RIFT_CORE_TICK_SOURCE, RIFT_CORE_FADE_TICKS, 3*20, RIFT_CORE_FADE_TICKS);

    public static void updateRiftCoreRenderTime(long tick, float partialTick) {
        RIFT_CORE_TICK_SOURCE.update(tick, partialTick);
    }

    public static void triggerRiftCoreHighlight() {
        int highlightMillis = DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
        if (highlightMillis < 0) {
            showRiftCoreUntil.reset();
            return;
        }

        showRiftCoreUntil.trigger();
    }

    public static <T extends Rift> T registerFunction(@NotNull T rift) {
        rift.register();
        return rift;
    }

    public static void runIfRiftAt(Location location, Consumer<Rift> consumer) {
        if(location.getBlockEntity() instanceof Rift rift) consumer.accept(rift);
    }

    public static record PortalPlane(Vec3 normal, Vec3 tangentX, Vec3 tangentY, Vec3 origin, double halfWidth, double height) {

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

            if (!(dotCurrent <= 0 && dotPrevious >= 0) && !(dotCurrent >= 0 && dotPrevious <= 0) || (dotCurrent == 0 && dotPrevious == 0)) {
                return false;
            }

            Vec3 positionChange = currentPos.subtract(previousPos);

            if (positionChange.lengthSqr() == 0) {
                return false;
            }

            Vec3 vecFromPreviousPosToPortalPlane = origin.subtract(previousPos);
            Vec3 pointOfIntersection = previousPos.add(
                    positionChange.scale(
                            vecFromPreviousPosToPortalPlane.dot(positionChange) /
                                    positionChange.dot(positionChange)
                    )
            );

            Vec3 intersectionRelativeToPortalPlane = pointOfIntersection.subtract(origin);
            double relativeIntersectionU = intersectionRelativeToPortalPlane.dot(tangentX);
            double relativeIntersectionV = intersectionRelativeToPortalPlane.dot(tangentY);

            return Math.abs(relativeIntersectionU) <= halfWidth
                    && relativeIntersectionV >= 0 && relativeIntersectionV <= height;
        }
    }
}
