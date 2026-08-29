package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.BlockPosUtil;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.RotationUtil;
import org.dimdev.dimdoors.util.LevelSpaceHelper;

import java.util.UUID;

/**
 * Sable implementation of {@link LevelSpaceHelper}.
 *
 * <p>Sable sub-levels carry their own coordinates, pose, velocity, and storage lifecycle, while
 * DimDoors works in world space. This class converts between the two and loads sub-levels that Sable
 * has unloaded.</p>
 *
 * <p>A plot stays occupied after its {@link ServerSubLevel} unloads, and the only way back to that
 * sub-level is the rift's tracking point, which holds the sub-level id and last saved pointer that
 * {@link SubLevelHoldingChunkMap#snatchAndLoad(GlobalSavedSubLevelPointer, java.util.UUID)} needs.
 * Sable's occupancy grid says whether a plot is taken but not which sub-level took it, so a world
 * position on its own is not enough to find one. Rifts made before this integration existed have no
 * tracking point, and {@link SableSubLevelStorage} searches storage to give them one.</p>
 */
public class SableLevelSpaceHelper extends LevelSpaceHelper {
    /**
     * Checks whether {@code pos} is in an occupied Sable plot with no live holder, first attempting to
     * load the containing sub-level.
     */
    public boolean isLevelSpaceUnavailable(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, pos);
        return isLevelSpaceUnavailableNow(level, pos);
    }

    /**
     * As {@link #isLevelSpaceUnavailable}, but without loading anything. That variant can reach disk,
     * so callers running per tick or per block update must use this one.
     */
    public boolean isLevelSpaceUnavailableNow(ServerLevel level, BlockPos pos) {
        var container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return false;
        }

        var chunkPos = new ChunkPos(pos);
        return SableSubLevelStorage.isOccupiedPlot(container, chunkPos) && container.getChunkHolder(chunkPos) == null;
    }

    /**
     * Returns the block entity at {@code pos}, first loading the sub-level that contains it.
     */
    @Override
    public BlockEntity getBlockEntity(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, pos);
        return level.getBlockEntity(pos);
    }

    /** @see #ensureSableSubLevelLoaded(ServerLevel, Vec3) */
    private void ensureSableSubLevelLoaded(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, Vec3.atCenterOf(pos));
    }

    /**
     * Ensures the Sable plot containing {@code pos} has a live holder or sub-level, loading the
     * sub-level through the tracking point of a rift registered there.
     *
     * @return {@code true} if nothing needed loading or the plot is now available; {@code false} if an
     * occupied plot could not be loaded
     */
    private boolean ensureSableSubLevelLoaded(ServerLevel level, Vec3 pos) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return true;
        }

        BlockPos blockPos = BlockPos.containing(pos);
        ChunkPos chunkPos = new ChunkPos(blockPos);

        if (!SableSubLevelStorage.isOccupiedPlot(container, chunkPos)) {
            return true;
        }

        if (container.getChunkHolder(chunkPos) != null && SableCompanion.INSTANCE.getContaining(level, pos) instanceof ServerSubLevel) {
            return true;
        }

        loadSubLevelAtRift(level, blockPos);

        return container.getChunkHolder(chunkPos) != null || SableCompanion.INSTANCE.getContaining(level, pos) instanceof ServerSubLevel;
    }

    /**
     * Loads the Sable sub-level containing {@code pos} using the tracking point of a rift registered
     * there.
     *
     * @return {@code true} when the sub-level is live afterwards
     */
    private boolean loadSubLevelAtRift(ServerLevel level, BlockPos pos) {
        var registry = RiftRegistry.getInstance();
        Location location = Location.ofWorld(level, pos);

        if (!registry.isRiftAt(location) || !(registry.getRift(location) instanceof SableRiftData sableRift)) {
            return false;
        }

        UUID trackingPointId = sableRift.dimdoors$getSableTrackingPoint();

        return trackingPointId != null && resolveTrackingPointPose(level, trackingPointId) != null;
    }

    /**
     * Rejects a teleport into an unavailable Sable level space, which would otherwise be treated as
     * ordinary world space and drop the entity into raw plot-grid coordinates.
     */
    @Override
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);
        if (isLevelSpaceUnavailable(level, blockPos)) {
            logUnavailableLevelSpace(level, blockPos, pos);
            throw new IllegalStateException("Teleport target " + pos + " in " + level.dimension().location() + " is inside Sable's plot grid, but no plot chunk holder is loaded there");
        }
    }

    /**
     * Records why a plot-grid position was judged unavailable, separating "no sub-level is loaded here"
     * from "a sub-level is loaded but this particular chunk has no holder", which the availability check
     * cannot distinguish because it mixes plot granularity with chunk granularity.
     */
    private void logUnavailableLevelSpace(ServerLevel level, BlockPos blockPos, Vec3 pos) {
        var container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }

        var chunkPos = new ChunkPos(blockPos);
        DimensionalDoors.LOGGER.warn("Sable level space unavailable at {}: chunk {}, occupiedPlot={}, plot={}, chunkHolder={}, containingSubLevel={}",
                pos, chunkPos,
                SableSubLevelStorage.isOccupiedPlot(container, chunkPos),
                container.getPlot(chunkPos),
                container.getChunkHolder(chunkPos),
                SableCompanion.INSTANCE.getContaining(level, blockPos));
    }

    /**
     * Prepares the Sable level space containing {@code pos} for rift creation, ensuring the sub-level
     * is live and its plot has a chunk holder for the rift's chunk.
     */
    @Override
    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return true;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        if (!container.inBounds(chunkPos) || container.getChunkHolder(chunkPos) != null) {
            return true;
        }

        if (!ensureSableSubLevelLoaded(level, Vec3.atCenterOf(pos))) {
            return false;
        }

        if (container.getChunkHolder(chunkPos) != null) {
            return true;
        }

        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        if (!(subLevel instanceof ServerSubLevel)) {
            loadSubLevelAtRift(level, pos);
            subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        }

        if (!(subLevel instanceof ServerSubLevel serverSubLevel)) {
            return false;
        }

        var plot = serverSubLevel.getPlot();
        ChunkPos localChunkPos = plot.toLocal(chunkPos);
        if (plot.getChunkHolder(localChunkPos) == null) {
            plot.newEmptyChunk(chunkPos);
        }

        return plot.getChunkHolder(localChunkPos) != null;
    }

    /**
     * Records where a rift sits inside its Sable sub-level, so the rift's pose can be recovered after
     * that sub-level unloads. Any existing tracking point is replaced rather than updated, so a moved
     * rift cannot retain a stale reference.
     */
    public void updateRiftTrackingPoint(ServerLevel level, Rift rift) {
        if (!(rift instanceof SableRiftData sableRift)) {
            return;
        }

        var trackingData = SubLevelTrackingPointSavedData.getOrLoad(level);
        UUID previousTrackingPoint = sableRift.dimdoors$getSableTrackingPoint();
        if (previousTrackingPoint != null) {
            trackingData.removeTrackingPoint(previousTrackingPoint);
            sableRift.dimdoors$setSableTrackingPoint(null);
        }

        UUID trackingPoint = null;
        Location location = rift.getLocation();

        if (location != null && location.world.equals(level.dimension())) {
            Vec3 localPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);

            if (SableCompanion.INSTANCE.getContaining(level, localPos) instanceof ServerSubLevel serverSubLevel) {
                trackingPoint = trackingData.generateTrackingPoint(localPos, serverSubLevel);
                sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
                logRecordedTrackingPoint(trackingData, trackingPoint, location, "an updated");
            }
        }

        if (previousTrackingPoint != null || trackingPoint != null) {
            RiftRegistry.getInstance().setDirty();
        }
    }

    public void removeRiftTrackingPoint(ServerLevel level, Rift rift) {
        if (!(rift instanceof SableRiftData sableRift)) {
            return;
        }

        UUID previousTrackingPoint = sableRift.dimdoors$getSableTrackingPoint();
        if (previousTrackingPoint == null) {
            return;
        }

        SubLevelTrackingPointSavedData.getOrLoad(level).removeTrackingPoint(previousTrackingPoint);
        sableRift.dimdoors$setSableTrackingPoint(null);
        RiftRegistry.getInstance().setDirty();
    }

    /**
     * Reports what a freshly recorded tracking point actually names, so that a later load failure can
     * be traced back to whether its pointer was already wrong when the tracking point was written.
     *
     * <p>Sable derives the pointer, not DimDoors, so a pointer logged as null here belongs to Sable's
     * save cycle rather than to anything DimDoors recorded.</p>
     */
    static void logRecordedTrackingPoint(SubLevelTrackingPointSavedData trackingData, UUID trackingPointId, Location location, String riftDescription) {
        TrackingPoint written = trackingData.getTrackingPoint(trackingPointId);

        if (written == null) {
            DimensionalDoors.LOGGER.warn("Sable tracking point {} for {} rift at {} could not be read back after being recorded", trackingPointId, riftDescription, location);
            return;
        }

        DimensionalDoors.LOGGER.info("Recorded Sable tracking point {} for {} rift at {}: sub-level {}, pointer {}", trackingPointId, riftDescription, location, written.subLevelID(), written.lastSavedSubLevelPointer());
    }

    /**
     * Resolves the Sable level space that should receive a projected teleport frame, loading it if it
     * is not already live.
     */
    private SubLevelAccess getTargetSubLevel(ServerLevel level, Location location, Vec3 pos) {
        Vec3 loadPos = getTargetSubLevelLoadPosition(level, location, pos);

        SubLevelAccess subLevel = probeTargetSubLevel(level, loadPos, pos);
        if (subLevel != null) {
            return subLevel;
        }

        ensureSableSubLevelLoaded(level, loadPos);
        return probeTargetSubLevel(level, loadPos, pos);
    }

    /**
     * Looks for a live sub-level at the load probe, then at the projected entity position.
     *
     * <p>The rift block is probed first because the entity position may be fractionally offset from
     * the rift or already advanced by movement.</p>
     */
    private SubLevelAccess probeTargetSubLevel(ServerLevel level, Vec3 loadPos, Vec3 pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, loadPos);

        if (subLevel != null || loadPos.equals(pos)) {
            return subLevel;
        }

        return SableCompanion.INSTANCE.getContaining(level, pos);
    }

    /**
     * Chooses the load probe: the target rift's block position when that rift belongs to this level,
     * and otherwise the projected entity position.
     */
    private Vec3 getTargetSubLevelLoadPosition(ServerLevel level, Location location, Vec3 pos) {
        return location != null && location.world.equals(level.dimension())
                ? Vec3.upFromBottomCenterOf(location.pos, 0.0)
                : pos;
    }

    /**
     * Projects a teleport frame through an occupied Sable plot whose level space is unavailable, by
     * loading it from the target rift's tracking point.
     *
     * @return the projected frame, or {@code null} if no rift or pose could be resolved, leaving the
     * caller to fall back to world space
     */
    private TeleportFrame projectUnloadedRiftTeleportFrame(ServerLevel level, Location targetLocation, Vec3 pos, Rotations angle, Vec3 velocity) {
        Vec3 loadPos = getTargetSubLevelLoadPosition(level, targetLocation, pos);

        // getTargetSubLevel has already attempted a load by this point, so only check the result.
        if (!isLevelSpaceUnavailableNow(level, BlockPos.containing(loadPos))) {
            DimensionalDoors.LOGGER.warn("Sable plot at load probe {} reports available, but no sub-level was found there; entity target is {}", loadPos, pos);
            return null;
        }

        Location location = findRiftLocation(level, targetLocation, pos);
        if (location == null) {
            DimensionalDoors.LOGGER.warn("No registered rift near {} in {}; cannot resolve its unloaded Sable sub-level", pos, level.dimension().location());
            return null;
        }

        Rift rift = RiftRegistry.getInstance().getRift(location);
        if (!(rift instanceof SableRiftData sableRift)) {
            DimensionalDoors.LOGGER.warn("Rift at {} is {}, which carries no Sable tracking data", location, rift == null ? "missing from the registry" : "not Sable-tracked");
            return null;
        }

        UUID trackingPointId = sableRift.dimdoors$getSableTrackingPoint();

        // A rift with no tracking point predates Sable integration. Record one now; from here on
        // it resolves through the ordinary path like every other rift.
        if (trackingPointId == null) {
            trackingPointId = SableSubLevelStorage.trackRiftFromStorage(level, rift, location);
        }

        Pose3dc pose = trackingPointId == null ? null : resolveTrackingPointPose(level, trackingPointId);

        if (pose == null) {
            return null;
        }

        return projectTeleportFrame(pose, pos, angle, pose.transformNormal(velocity));
    }

    /**
     * Records a tracking point for a rift that has none, while its sub-level is live.
     *
     * <p>{@code addRift} does not fire for rifts restored from saved data, so those rifts are never
     * tracked by the ordinary path. Does nothing if the rift already has a tracking point or does not
     * lie inside a sub-level.</p>
     */
    private void trackRiftIfUntracked(ServerLevel level, Location location) {
        var registry = RiftRegistry.getInstance();
        if (location == null || !registry.isRiftAt(location)) {
            return;
        }

        Rift rift = registry.getRift(location);
        if (!(rift instanceof SableRiftData sableRift) || sableRift.dimdoors$getSableTrackingPoint() != null) {
            return;
        }

        updateRiftTrackingPoint(level, rift);
    }

    /**
     * Identifies the rift a projected destination frame belongs to, preferring the explicit target
     * location and otherwise searching nearby vertical positions to tolerate small offsets.
     */
    private Location findRiftLocation(ServerLevel level, Location targetLocation, Vec3 pos) {
        var registry = RiftRegistry.getInstance();

        if (targetLocation != null && targetLocation.world.equals(level.dimension()) && registry.isRiftAt(targetLocation)) {
            return targetLocation;
        }

        return BlockPosUtil.nearbyVertical(BlockPos.containing(pos), candidate -> {
            Location location = Location.ofWorld(level, candidate);
            return registry.isRiftAt(location) ? location : null;
        });
    }

    /**
     * Resolves a pose from a saved Sable tracking point, loading the sub-level if it is not live.
     *
     * <p>A holding entry is loaded directly rather than through its pointer. Sable stamps an entry
     * unloaded at runtime with the sub-level's last serialization pointer, which names where it was
     * last written to disk rather than the holding chunk it now sits in, and {@code snatchAndLoad}
     * resolves the chunk from that pointer and so would look in the wrong place. Sable reconciles the
     * two in {@code saveAll}, so the mismatch lasts only until the next save. The tracking point's
     * pointer is the fallback for a sub-level that has no holding entry at all.</p>
     *
     * @return the sub-level's pose, or {@code null} if the tracking point is unusable, nothing can be
     * found to load, or the sub-level does not go live
     */
    private Pose3dc resolveTrackingPointPose(ServerLevel level, UUID trackingPointId) {
        TrackingPoint trackingPoint = SubLevelTrackingPointSavedData.getOrLoad(level).getTrackingPoint(trackingPointId);
        if (trackingPoint == null || !trackingPoint.inSubLevel()) {
            return null;
        }

        var loadedSubLevel = SableCompanion.INSTANCE.getContaining(level, trackingPoint.point());
        if (loadedSubLevel instanceof ServerSubLevel serverSubLevel) {
            return serverSubLevel.logicalPose();
        }

        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }

        UUID subLevelId = trackingPoint.subLevelID();
        if (subLevelId == null) {
            DimensionalDoors.LOGGER.warn("Sable tracking point {} names no sub-level, so it cannot be loaded", trackingPointId);
            return null;
        }

        SubLevelHoldingChunkMap holdingChunkMap = container.getHoldingChunkMap();
        HoldingSubLevel holdingSubLevel = holdingChunkMap.getHoldingSubLevel(subLevelId);

        GlobalSavedSubLevelPointer pointer = trackingPoint.lastSavedSubLevelPointer();

        if (holdingSubLevel != null) {
            if (!SableSubLevelStorage.snatchAndLoadFromOwningChunk(holdingChunkMap, subLevelId)) {
                SableSubLevelStorage.loadChain(holdingChunkMap, holdingSubLevel);
            }
        } else if (pointer != null) {
            holdingChunkMap.snatchAndLoad(pointer, subLevelId);
        } else {
            DimensionalDoors.LOGGER.warn("Sable sub-level {} has no holding entry and no saved pointer, so it cannot be loaded", subLevelId);
            return null;
        }

        if (container.getSubLevel(subLevelId) instanceof ServerSubLevel serverSubLevel) {
            DimensionalDoors.LOGGER.info("Sable sub-level {} is live with pose {}", subLevelId, serverSubLevel.logicalPose());
            return serverSubLevel.logicalPose();
        }

        DimensionalDoors.LOGGER.warn("Sable sub-level {} did not become live after being loaded from {}", subLevelId,
                holdingSubLevel != null ? "its holding entry" : "pointer " + pointer + " on tracking point " + trackingPointId);
        return null;
    }

    /**
     * Converts an outgoing teleport frame from world space into the source Sable level space.
     *
     * <p>Sable reports sub-level velocity in blocks per second and entity delta movement is in blocks
     * per tick, so the inherited velocity is divided by 20 before being removed.</p>
     */
    @Override
    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        var sourceSubLevel = SableCompanion.INSTANCE.getContaining(level, sourcePos);

        if (sourceSubLevel == null) {
            return new TeleportFrame(pos, angle, velocity);
        }

        var pose = sourceSubLevel.logicalPose();

        Vec3 localPos = pose.transformPositionInverse(pos);

        Vec3 inheritedVelocity = SableCompanion.INSTANCE
                .getVelocity(level, sourceSubLevel, localPos)
                .scale(1.0 / 20.0);
        Vec3 localVelocity = pose.transformNormalInverse(velocity.subtract(inheritedVelocity));

        Rotations localAngle = transformAngle(pose, angle, true);

        return new TeleportFrame(localPos, localAngle, localVelocity);
    }

    /**
     * Projects an incoming teleport frame into the target world space, returning it unchanged only
     * when no Sable-specific projection applies.
     *
     * <p>Reaching a live sub-level is also the only opportunity to repair a rift missing its tracking
     * point; see {@link #trackRiftIfUntracked}.</p>
     */
    @Override
    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        var subLevel = getTargetSubLevel(level, location, pos);

        if (subLevel == null) {
            TeleportFrame unloadedFrame = projectUnloadedRiftTeleportFrame(level, location, pos, angle, velocity);
            if (unloadedFrame != null) {
                return unloadedFrame;
            }

            validateTeleportDestination(level, pos);
            return new TeleportFrame(pos, angle, velocity);
        }

        if (subLevel instanceof ServerSubLevel) {
            trackRiftIfUntracked(level, location);
        }

        var pose = subLevel.logicalPose();

        Vec3 worldVelocity = pose.transformNormal(velocity);

        // Sable reports velocity in blocks/second; entity delta movement is blocks/tick.
        Vec3 inheritedVelocity = SableCompanion.INSTANCE
                .getVelocity(level, subLevel, pos)
                .scale(1.0 / 20.0);

        worldVelocity = worldVelocity.add(inheritedVelocity);

        return projectTeleportFrame(pose, pos, angle, worldVelocity);
    }

    /**
     * Applies a known Sable pose to a teleport frame expressed in Sable level space.
     *
     * <p>The caller must supply velocity already transformed into target world space because the
     * loaded and unloaded paths recover inherited Sable velocity differently.</p>
     */
    private TeleportFrame projectTeleportFrame(Pose3dc pose, Vec3 pos, Rotations angle, Vec3 worldVelocity) {
        Vec3 worldPos = pose.transformPosition(pos);
        Rotations worldAngle = transformAngle(pose, angle, false);

        return new TeleportFrame(worldPos, worldAngle, worldVelocity);
    }

    /**
     * Transforms an Euler rotation through a Sable pose by way of forward and up basis vectors,
     * which avoids the discontinuities of composing yaw, pitch, and roll directly.
     *
     * <p>Degenerate forward or up vectors fall back to the safest available orientation.</p>
     */
    private Rotations transformAngle(Pose3dc pose, Rotations angle, boolean inverse) {
        TransformationMatrix3d rotator = TransformationMatrix3d.builder().rotate(angle).build();
        Vec3 forward = rotator.transform(new Vec3(0.0, 0.0, 1.0));
        Vec3 up = rotator.transform(new Vec3(0.0, 1.0, 0.0));

        forward = inverse ? pose.transformNormalInverse(forward) : pose.transformNormal(forward);
        up = inverse ? pose.transformNormalInverse(up) : pose.transformNormal(up);

        if (forward.lengthSqr() < 1.0E-12) {
            return angle;
        }

        Vec3 normalizedForward = forward.normalize();
        Vec3 projectedUp = up.subtract(normalizedForward.scale(up.dot(normalizedForward)));
        if (projectedUp.lengthSqr() < 1.0E-12) {
            return RotationUtil.rotFromDirection(normalizedForward, angle.getZ());
        }

        return MathUtil.eulerAngle(normalizedForward, projectedUp.normalize());
    }

    /**
     * Converts an entity's collision box and movement history into the coordinates of the Sable
     * sub-level tracking it.
     */
    @Override
    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        var trackingSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(entity);
        if (trackingSubLevel == null) {
            return new AfterBlockData(box, previousPos, currentPos);
        }

        var pose = trackingSubLevel.logicalPose();

        previousPos = pose.transformPositionInverse(previousPos);
        currentPos = pose.transformPositionInverse(currentPos);

        box = inverseTransformBox(pose, box);

        return new AfterBlockData(box, previousPos, currentPos);
    }

    /**
     * Inverse-transforms an axis-aligned box through a Sable pose. A rotated pose does not map one
     * axis-aligned box onto another, so all eight corners are transformed and their envelope taken.
     */
    private AABB inverseTransformBox(Pose3dc pose, AABB box) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (int corner = 0; corner < 8; corner++) {
            Vec3 transformed = pose.transformPositionInverse(new Vec3(
                    (corner & 1) == 0 ? box.minX : box.maxX,
                    (corner & 2) == 0 ? box.minY : box.maxY,
                    (corner & 4) == 0 ? box.minZ : box.maxZ
            ));

            minX = Math.min(minX, transformed.x);
            minY = Math.min(minY, transformed.y);
            minZ = Math.min(minZ, transformed.z);
            maxX = Math.max(maxX, transformed.x);
            maxY = Math.max(maxY, transformed.y);
            maxZ = Math.max(maxZ, transformed.z);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
