package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelSerializer;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkAccessor;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkMapAccessor;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.util.RotationUtil;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.joml.Vector3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Active Sable integration for DimDoors.
 *
 * <p>This class is the runtime implementation of {@link SableHelper} used when Sable is present.
 * It bridges DimDoors' rift, teleport, collision, and block-access code with Sable's dynamic
 * sub-level model. Sable sub-levels have their own local coordinate space, pose, velocity, and
 * plot storage, while DimDoors usually reasons in normal Minecraft level coordinates.</p>
 *
 * <p>The helper has three main responsibilities:</p>
 *
 * <ol>
 *     <li>Convert positions, rotations, velocities, and entity collision data between world space
 *     and Sable sub-level local space.</li>
 *     <li>Resolve rift targets that may be inside loaded, unloaded, or half-loaded Sable plots.</li>
 *     <li>Materialize stored Sable sub-level data when DimDoors needs immediate access to a live
 *     {@link ServerSubLevel} or {@code PlotChunkHolder}.</li>
 * </ol>
 *
 * <h2>Loaded and stored sub-levels</h2>
 *
 * <p>A Sable plot can be marked occupied even when the live {@link ServerSubLevel} is not currently
 * present in the level. In that case Sable keeps serialized {@link SubLevelData} in holding-chunk
 * storage. DimDoors still needs to teleport into that plot, create rifts there, and resolve tracking
 * points, so this class can look up stored sub-level data and recover the pose needed for projection.</p>
 *
 * <h2>Teleport frame flow</h2>
 *
 * <p>DimDoors splits teleport frame conversion into two directions:</p>
 *
 * <ul>
 *     <li>{@link #sourceTeleportFrame(ServerLevel, BlockPos, Entity, Vec3, Rotations, Vec3)} converts
 *     an entity leaving a Sable sub-level from world-space motion into sub-level-local motion.</li>
 *     <li>{@link #projectTeleportFrame(ServerLevel, Location, Vec3, Rotations, Vec3)} converts a
 *     frame entering a target level from local rift-space into the target Sable/world pose.</li>
 * </ul>
 *
 * <p>Projection prefers the live loaded sub-level path. If the target plot is occupied but not loaded,
 * the helper asks Sable's holding chunk map to process the stored sub-level. If Sable still does not
 * materialize the live sub-level immediately, the helper directly loads the stored data as a fallback
 * and removes only runtime holding entries so the next Sable tick does not double-load the same data.</p>
 *
 * <h2>Persistent storage safety</h2>
 *
 * <p>Runtime cleanup must never remove saved pointers from {@code SubLevelHoldingChunk#getSubLevelPointers()}.
 * Those pointers are persistent storage metadata. Removing them can orphan a stored sub-level on save.
 * Cleanup here only removes in-memory queue entries such as {@code allHoldingSubLevels} and
 * {@code loadedHoldingSubLevels} after the helper has manually materialized a sub-level.</p>
 */
public class ActiveSableHelper extends SableHelper {
    private static final Pattern SABLE_REGION_FILE_PATTERN = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.slvlr");
    private static Vector3d scratch = new Vector3d();

    /**
     * Projects a world-space vector out of whatever Sable sub-level currently contains it.
     *
     * <p>If {@code pos} is not inside a Sable sub-level, Sable returns the original world-space
     * position. This is used by generic DimDoors code that needs coordinates independent of
     * Sable's local plot transforms.</p>
     */
    @Override
    public Vec3 projectFrom(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos);
    }

    /**
     * Block-position variant of {@link #projectFrom(Level, Vec3)}.
     *
     * <p>A shared scratch vector is used to avoid allocating a temporary JOML vector for every block
     * query. The returned position is rounded through {@link BlockPos#containing(double, double, double)}.</p>
     */
    @Override
    public BlockPos projectFrom(Level level, BlockPos pos) {
        scratch.set(pos.getX(), pos.getY(), pos.getZ());
        SableCompanion.INSTANCE.projectOutOfSubLevel(level, scratch);
        return BlockPos.containing(scratch.x(), scratch.y(), scratch.z());
    }

    /**
     * Converts a world-space point into the local coordinates of the Sable sub-level containing it.
     *
     * <p>If no sub-level contains the point, the original position is returned unchanged.</p>
     */
    @Override
    public Vec3 projectTo(ServerLevel level, Vec3 pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(pos);
        }

        return pos;
    }

    /**
     * Mutable-vector variant of {@link #projectTo(ServerLevel, Vec3)}.
     *
     * <p>The vector is transformed in place when it is inside a Sable sub-level.</p>
     */
    @Override
    public void projectTo(ServerLevel level, Vector3d pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            subLevel.logicalPose().transformPositionInverse(pos);
        }
    }

    /**
     * Checks whether {@code pos} lies inside an occupied Sable plot whose live chunk holder is missing.
     *
     * <p>This is the signal that a plot exists and should contain a sub-level, but Sable has not
     * materialized the runtime holder for that plot yet.</p>
     */
    @Override
    public boolean isMissingSablePlotHolder(ServerLevel level, BlockPos pos) {
        var container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return false;
        }

        var chunkPos = new ChunkPos(pos);
        return isOccupiedSablePlot(container, chunkPos) && container.getChunkHolder(chunkPos) == null;
    }

    /**
     * Returns a block entity after giving Sable a chance to materialize the target plot first.
     *
     * <p>DimDoors may ask for block entities inside Sable plot coordinates. Without the load step,
     * the vanilla lookup may run before the plot holder exists.</p>
     */
    @Override
    public BlockEntity getBlockEntity(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, pos);
        return level.getBlockEntity(pos);
    }

    /**
     * Block-position overload for {@link #ensureSableSubLevelLoaded(ServerLevel, Vec3)}.
     */
    @Override
    public boolean ensureSableSubLevelLoaded(ServerLevel level, BlockPos pos) {
        return ensureSableSubLevelLoaded(level, Vec3.atCenterOf(pos));
    }

    /**
     * Ensures that the occupied Sable plot containing {@code pos} has a live holder or live sub-level.
     *
     * <p>Non-Sable levels, chunks outside the plot grid, and unoccupied plots are treated as already
     * valid. Occupied plots without a holder are resolved by searching Sable's holding storage and
     * materializing the matching stored sub-level.</p>
     *
     * @return {@code true} when no Sable load is needed or a live holder/sub-level is available;
     * {@code false} when an occupied plot exists but cannot be materialized
     */
    @Override
    public boolean ensureSableSubLevelLoaded(ServerLevel level, Vec3 pos) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return true;
        }

        ChunkPos chunkPos = new ChunkPos(BlockPos.containing(pos));
        if (!container.inBounds(chunkPos)) {
            return true;
        }

        if (!isOccupiedSablePlot(container, chunkPos)) {
            return true;
        }

        if (container.getChunkHolder(chunkPos) != null) {
            return true;
        }

        if (SableCompanion.INSTANCE.getContaining(level, pos) instanceof ServerSubLevel) {
            return true;
        }

        forceLoadStoredSubLevelAt(level, pos);

        return container.getChunkHolder(chunkPos) != null
                || SableCompanion.INSTANCE.getContaining(level, pos) instanceof ServerSubLevel;
    }

    /**
     * Validates that a teleport destination is not inside an occupied-but-unloaded Sable plot.
     *
     * <p>This is called before falling back to a plain world-space teleport frame. If the position
     * is inside Sable's occupied plot grid and no holder can be loaded, teleporting as if it were
     * normal world space would place the entity into an invalid unloaded plot.</p>
     */
    @Override
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
        ensureSableSubLevelLoaded(level, pos);
        if (isMissingSablePlotHolder(level, BlockPos.containing(pos))) {
            throw new IllegalStateException("Teleport target " + pos + " in " + level.dimension().location() + " is inside Sable's plot grid, but no plot chunk holder is loaded there");
        }
    }

    /**
     * Prepares a Sable plot for creating or updating a DimDoors rift at {@code pos}.
     *
     * <p>If the position is outside Sable's plot grid, no special handling is needed. For occupied
     * plots, this method ensures the sub-level is loaded and that the plot has a chunk holder for
     * the local chunk where the rift will be placed.</p>
     */
    @Override
    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return true;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        if (!container.inBounds(chunkPos)) {
            return true;
        }

        if (container.getChunkHolder(chunkPos) != null) {
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
            forceLoadStoredSubLevelAt(level, Vec3.atCenterOf(pos));
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
     * Updates the Sable tracking point associated with a DimDoors rift.
     *
     * <p>Tracking points let unloaded-rift projection recover the latest pose of a sub-level even
     * after the rift's containing plot is no longer live. Existing tracking points are replaced so
     * the rift does not keep stale pose references after movement or relocation.</p>
     */
    @Override
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

        Location location = rift.getLocation();
        if (location == null || !location.world.equals(level.dimension())) {
            if (previousTrackingPoint != null) {
                DimensionalRegistry.setDirty();
            }
            return;
        }

        Vec3 localPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
        var subLevel = SableCompanion.INSTANCE.getContaining(level, localPos);
        if (!(subLevel instanceof ServerSubLevel serverSubLevel)) {
            if (previousTrackingPoint != null) {
                DimensionalRegistry.setDirty();
            }
            return;
        }

        UUID trackingPoint = trackingData.generateTrackingPoint(localPos, serverSubLevel);
        sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
        DimensionalRegistry.setDirty();
    }

    /**
     * Removes the Sable tracking point associated with a DimDoors rift.
     *
     * <p>This is called when the rift no longer needs to track a Sable sub-level pose.</p>
     */
    @Override
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
        DimensionalRegistry.setDirty();
    }

    /**
     * Resolves the loaded Sable sub-level that should receive a projected teleport frame.
     *
     * <p>The lookup deliberately probes the rift block location when a target {@link Location} is
     * known. The entity's projected position can be offset from the rift block by fractions of a
     * block or by player movement, while the rift block itself is the stable coordinate that should
     * be inside the target plot.</p>
     *
     * <p>If the load-position probe fails and the load position is different from the original
     * projected entity position, the method performs one fallback lookup at the original position.</p>
     */
    private SubLevelAccess getTargetSubLevel(ServerLevel level, Location location, Vec3 pos) {
        Vec3 loadPos = getTargetSubLevelLoadPosition(level, location, pos);

        var loadPosSubLevel = SableCompanion.INSTANCE.getContaining(level, loadPos);
        if (loadPosSubLevel != null) {
            return loadPosSubLevel;
        }

        if (loadPos == pos) {
            return null;
        }

        return SableCompanion.INSTANCE.getContaining(level, pos);
    }

    /**
     * Chooses the coordinate used to probe/load the target Sable sub-level.
     *
     * <p>When the rift location is in the same level as the projection target, the rift block center
     * is used instead of the entity's exact projected position. Otherwise the entity position is the
     * only available load probe.</p>
     */
    private Vec3 getTargetSubLevelLoadPosition(ServerLevel level, Location location, Vec3 pos) {
        return location != null && location.world.equals(level.dimension())
                ? Vec3.upFromBottomCenterOf(location.pos, 0.0)
                : pos;
    }

    /**
     * Projects a teleport frame through an occupied Sable plot that is not currently materialized.
     *
     * <p>This is the fallback path after normal target sub-level resolution fails. It only runs when
     * the target load position is inside an occupied Sable plot with no live holder. The method then
     * tries to recover a pose without requiring the live sub-level to already exist.</p>
     *
     * <p>Pose recovery order:</p>
     *
     * <ol>
     *     <li>Use the rift's saved Sable tracking point if present.</li>
     *     <li>Resolve an untracked rift by finding the stored plot containing the rift.</li>
     *     <li>Resolve the stored plot containing the target load position.</li>
     * </ol>
     *
     * <p>If a pose is recovered, the frame is projected through that pose using the same lower-level
     * transform path as loaded sub-level projection.</p>
     */
    private TeleportFrame projectUnloadedRiftTeleportFrame(ServerLevel level, Location targetLocation, Vec3 pos, Rotations angle, Vec3 velocity) {
        Vec3 loadPos = getTargetSubLevelLoadPosition(level, targetLocation, pos);

        if (!isMissingSablePlotHolder(level, BlockPos.containing(loadPos))) {
            return null;
        }

        Pose3dc pose = null;

        Location location = findRiftLocation(level, targetLocation, pos);
        if (location != null) {
            Rift rift = DimensionalRegistry.getRiftRegistry().getRift(location);

            if (rift instanceof SableRiftData sableRift) {
                UUID trackingPointId = sableRift.dimdoors$getSableTrackingPoint();

                pose = trackingPointId == null ? null : resolveTrackingPointPose(level, trackingPointId);
                if (pose == null) {
                    pose = resolveUntrackedRiftPose(level, rift, location);
                }
            }
        }

        if (pose == null) {
            pose = resolveStoredPlotPose(level, loadPos);
        }

        if (pose == null) {
            return null;
        }

        return projectTeleportFrame(pose, pos, angle, pose.transformNormal(velocity));
    }

    /**
     * Attempts to identify the DimDoors rift associated with a projected target frame.
     *
     * <p>The explicit target location is preferred when it is in the same level and still contains
     * a registered rift. If unavailable, the current block, block below, and block above the projected
     * position are checked to tolerate small vertical or fractional offsets around the rift block.</p>
     */
    private Location findRiftLocation(ServerLevel level, Location targetLocation, Vec3 pos) {
        var registry = DimensionalRegistry.getRiftRegistry();

        if (targetLocation != null && targetLocation.world.equals(level.dimension()) && registry.isRiftAt(targetLocation)) {
            return targetLocation;
        }

        BlockPos blockPos = BlockPos.containing(pos);
        Location location = Location.ofWorld(level, blockPos);
        if (registry.isRiftAt(location)) {
            return location;
        }

        Location below = Location.ofWorld(level, blockPos.below());
        if (registry.isRiftAt(below)) {
            return below;
        }

        Location above = Location.ofWorld(level, blockPos.above());
        if (registry.isRiftAt(above)) {
            return above;
        }

        return null;
    }

    /**
     * Resolves a pose from a saved Sable tracking point.
     *
     * <p>A tracking point may refer to a currently loaded sub-level, a runtime holding sub-level, or
     * a persisted saved sub-level pointer. Loaded sub-levels provide the freshest live pose. Stored
     * data is force-processed first, then used directly as a pose fallback if Sable still does not
     * expose a live {@link ServerSubLevel}.</p>
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

        SubLevelData data = null;
        GlobalSavedSubLevelPointer pointer = null;

        if (trackingPoint.subLevelID() != null) {
            var holdingSubLevel = container.getHoldingChunkMap().getHoldingSubLevel(trackingPoint.subLevelID());
            if (holdingSubLevel != null) {
                data = holdingSubLevel.data();
                pointer = holdingSubLevel.pointer();
            }
        }

        if (data == null) {
            pointer = trackingPoint.lastSavedSubLevelPointer();
            if (pointer == null) {
                return null;
            }

            data = container.getHoldingChunkMap().getStorage().attemptLoadSubLevel(pointer.chunkPos(), pointer.local());
            if (data == null) {
                return null;
            }
        }

        forceLoadTeleportSubLevel(level, container, data, pointer);

        loadedSubLevel = SableCompanion.INSTANCE.getContaining(level, trackingPoint.point());
        if (loadedSubLevel instanceof ServerSubLevel serverSubLevel) {
            return serverSubLevel.logicalPose();
        }

        return data.pose();
    }

    /**
     * Resolves and initializes pose tracking for a Sable rift that does not yet have a tracking point.
     *
     * <p>The rift block is converted to a local Sable lookup position, then the containing stored
     * sub-level is found. If loading succeeds, a new tracking point is created so future unloaded
     * projections can skip the slower storage search.</p>
     */
    private Pose3dc resolveUntrackedRiftPose(ServerLevel level, Rift rift, Location location) {
        if (!(rift instanceof SableRiftData sableRift)) {
            return null;
        }

        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }

        Vec3 localRiftPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
        StoredSubLevel storedSubLevel = findStoredSubLevelContaining(container, localRiftPos);
        if (storedSubLevel == null) {
            return null;
        }

        forceLoadTeleportSubLevel(level, container, storedSubLevel.data(), storedSubLevel.pointer());

        var loadedSubLevel = SableCompanion.INSTANCE.getContaining(level, localRiftPos);
        if (!(loadedSubLevel instanceof ServerSubLevel serverSubLevel)) {
            return storedSubLevel.data().pose();
        }

        UUID trackingPoint = SubLevelTrackingPointSavedData.getOrLoad(level).generateTrackingPoint(localRiftPos, serverSubLevel);
        sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
        DimensionalRegistry.setDirty();

        return serverSubLevel.logicalPose();
    }

    /**
     * Resolves a pose from the stored Sable plot containing {@code pos}.
     *
     * <p>This path is used when no rift tracking information is available. It attempts to materialize
     * the stored sub-level first. If the live sub-level becomes available, its current logical pose is
     * used; otherwise the serialized pose is returned as a best-effort fallback.</p>
     */
    private Pose3dc resolveStoredPlotPose(ServerLevel level, Vec3 pos) {
        StoredSubLevel storedSubLevel = forceLoadStoredSubLevelAt(level, pos);
        if (storedSubLevel == null) {
            return null;
        }

        var loadedSubLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        if (loadedSubLevel instanceof ServerSubLevel serverSubLevel) {
            return serverSubLevel.logicalPose();
        }

        return storedSubLevel.data().pose();
    }

    /**
     * Removes a directly-loaded sub-level from Sable's runtime holding queues.
     *
     * <p>This is used only after this helper manually calls {@link SubLevelSerializer#fullyLoad(ServerLevel, SubLevelData)}.
     * The direct load creates a live {@link ServerSubLevel} immediately, but Sable's holding map may
     * still contain the same {@link HoldingSubLevel} in memory. If left queued, Sable's next
     * {@code processChanges()} call can try to load the same sub-level again and fail with a duplicate
     * plot allocation.</p>
     *
     * <p>The search intentionally scans all loaded holding chunks, not just the stored pointer's chunk.
     * Moved or partially serialized sub-levels can temporarily have a {@code null} pointer or a pointer
     * that no longer matches the in-memory holding chunk that still has the queued entry.</p>
     *
     * <p>This method removes runtime map entries only. It must not remove saved pointer entries from
     * {@code SubLevelHoldingChunk#getSubLevelPointers()}, because those pointers are persistent storage
     * metadata and removing them can orphan the saved sub-level.</p>
     */
    private void removeRuntimeHoldingSubLevel(ServerSubLevelContainer container, UUID uuid) {
        SubLevelHoldingChunkMapAccessor holdingMapAccessor =
                (SubLevelHoldingChunkMapAccessor) container.getHoldingChunkMap();

        holdingMapAccessor
                .dimdoors$getAllHoldingSubLevels()
                .remove(uuid);

        for (SubLevelHoldingChunk holdingChunk : holdingMapAccessor.dimdoors$getLoadedHoldingChunks().values()) {
            ((SubLevelHoldingChunkAccessor) holdingChunk)
                    .dimdoors$getLoadedHoldingSubLevels()
                    .remove(uuid);
        }
    }

    /**
     * Attempts to materialize the stored Sable sub-level containing {@code pos}.
     *
     * <p>The method is used when DimDoors knows a position is inside an occupied Sable plot, but Sable
     * has not provided a live holder or live {@link ServerSubLevel}. It first searches runtime holding
     * entries, then persisted holding-region files, for stored sub-level data whose plot tag matches
     * the target Sable plot.</p>
     *
     * <p>After a stored sub-level is found, the normal Sable path is tried first by requesting its
     * holding chunk and calling {@link #forceLoadTeleportSubLevel(ServerLevel, ServerSubLevelContainer, SubLevelData, GlobalSavedSubLevelPointer)}.
     * If Sable still does not materialize a live sub-level immediately, this method directly loads the
     * stored {@link SubLevelData} through {@link SubLevelSerializer#fullyLoad(ServerLevel, SubLevelData)}.</p>
     *
     * <p>Direct loading is followed by runtime-only holding cleanup. This prevents Sable's next
     * {@code processChanges()} tick from loading the same holding entry again and throwing duplicate
     * plot allocation errors. This is especially important for moved sub-levels, whose stored pointer
     * may be stale or temporarily {@code null}.</p>
     *
     * <p>This method intentionally does not remove saved holding pointers from persistent storage.</p>
     *
     * @return the stored sub-level data that was found, or {@code null} if no matching stored sub-level exists
     */
    private StoredSubLevel forceLoadStoredSubLevelAt(ServerLevel level, Vec3 pos) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }

        StoredSubLevel storedSubLevel = findStoredSubLevelContaining(container, pos);
        if (storedSubLevel == null) {
            return null;
        }

        forceLoadTeleportSubLevel(level, container, storedSubLevel.data(), storedSubLevel.pointer());

        var containingAfter = SableCompanion.INSTANCE.getContaining(level, pos);
        var holderAfter = container.getChunkHolder(new ChunkPos(BlockPos.containing(pos)));

        if (!(containingAfter instanceof ServerSubLevel) && holderAfter == null) {
            ServerSubLevel loadedSubLevel = SubLevelSerializer.fullyLoad(level, storedSubLevel.data());

            if (loadedSubLevel != null) {
                if (storedSubLevel.pointer() != null) {
                    loadedSubLevel.setLastSerializationPointer(storedSubLevel.pointer());
                }

                removeRuntimeHoldingSubLevel(container, storedSubLevel.data().uuid());
            }
        }

        return storedSubLevel;
    }

    /**
     * Finds serialized Sable sub-level data for the occupied plot containing {@code pos}.
     *
     * <p>The lookup converts the target chunk into Sable plot coordinates, checks already-loaded
     * holding sublevels first, then scans Sable's holding region files. Region scanning is slower but
     * allows DimDoors to recover stored plot data even when Sable has not loaded the relevant holding
     * chunk into memory yet.</p>
     */
    private StoredSubLevel findStoredSubLevelContaining(ServerSubLevelContainer container, Vec3 pos) {
        ChunkPos targetChunk = new ChunkPos(BlockPos.containing(pos));
        if (!isOccupiedSablePlot(container, targetChunk)) {
            return null;
        }

        int targetPlotX = (targetChunk.x >> container.getLogPlotSize()) - container.getOrigin().x;
        int targetPlotZ = (targetChunk.z >> container.getLogPlotSize()) - container.getOrigin().y;

        StoredSubLevel loadedHoldingSubLevel = findLoadedHoldingSubLevelContaining(container, targetPlotX, targetPlotZ);
        if (loadedHoldingSubLevel != null) {
            return loadedHoldingSubLevel;
        }

        Path folder = container.getHoldingChunkMap().getStorage().getFolder();
        if (!Files.isDirectory(folder)) {
            return null;
        }

        try (var paths = Files.newDirectoryStream(folder, "*.slvlr")) {
            for (Path path : paths) {
                Matcher matcher = SABLE_REGION_FILE_PATTERN.matcher(path.getFileName().toString());
                if (!matcher.matches()) {
                    continue;
                }

                int regionX = Integer.parseInt(matcher.group(1));
                int regionZ = Integer.parseInt(matcher.group(2));
                StoredSubLevel storedSubLevel = findStoredSubLevelContaining(container, targetPlotX, targetPlotZ, regionX, regionZ);
                if (storedSubLevel != null) {
                    return storedSubLevel;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return null;
    }

    /**
     * Searches Sable's in-memory holding sub-level map for data stored in the target plot.
     *
     * <p>This is preferred over disk scanning because the holding map may contain fresh data for a
     * recently moved or unloaded sub-level that has not been fully flushed to region storage yet.</p>
     */
    private StoredSubLevel findLoadedHoldingSubLevelContaining(ServerSubLevelContainer container, int targetPlotX, int targetPlotZ) {
        var holdingSubLevels = ((SubLevelHoldingChunkMapAccessor) container.getHoldingChunkMap()).dimdoors$getAllHoldingSubLevels();
        for (HoldingSubLevel holdingSubLevel : holdingSubLevels.values()) {
            SubLevelData data = holdingSubLevel.data();
            if (isStoredInPlot(data, targetPlotX, targetPlotZ)) {
                return new StoredSubLevel(data, holdingSubLevel.pointer());
            }
        }

        return null;
    }

    /**
     * Searches one Sable holding-region file for stored sub-level data in the target plot.
     *
     * <p>Each region contains up to 32 by 32 holding chunks. The method loads holding chunks and then
     * resolves their saved sub-level pointers until it finds a stored sub-level whose serialized plot
     * tag matches {@code targetPlotX,targetPlotZ}.</p>
     */
    private StoredSubLevel findStoredSubLevelContaining(ServerSubLevelContainer container, int targetPlotX, int targetPlotZ, int regionX, int regionZ) {
        for (int localX = 0; localX < 32; localX++) {
            for (int localZ = 0; localZ < 32; localZ++) {
                ChunkPos holdingChunkPos = new ChunkPos((regionX << 5) + localX, (regionZ << 5) + localZ);
                var holdingChunk = container.getHoldingChunkMap().getStorage().attemptLoadHoldingChunk(holdingChunkPos);
                if (holdingChunk == null) {
                    continue;
                }

                for (SavedSubLevelPointer pointer : holdingChunk.getSubLevelPointers()) {
                    SubLevelData data = container.getHoldingChunkMap().getStorage().attemptLoadSubLevel(holdingChunkPos, pointer);
                    if (data == null) {
                        continue;
                    }

                    if (isStoredInPlot(data, targetPlotX, targetPlotZ)) {
                        return new StoredSubLevel(data, new GlobalSavedSubLevelPointer(holdingChunkPos, pointer.storageIndex(), pointer.subLevelIndex()));
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks whether serialized sub-level data belongs to the requested Sable plot coordinate.
     *
     * <p>The plot coordinate is read from Sable's serialized {@code plot} tag rather than inferred
     * from bounds, because moved and rotated sub-levels may have bounds that span or shift across
     * multiple chunks.</p>
     */
    private boolean isStoredInPlot(SubLevelData data, int targetPlotX, int targetPlotZ) {
        if (!data.fullTag().contains("plot", Tag.TAG_COMPOUND)) {
            return false;
        }

        var plotTag = data.fullTag().getCompound("plot");
        return plotTag.getInt("plot_x") == targetPlotX && plotTag.getInt("plot_z") == targetPlotZ;
    }

    /**
     * Returns whether a chunk belongs to an occupied Sable plot.
     *
     * <p>Sable stores plot occupancy in plot coordinates, not vanilla chunk coordinates. This method
     * converts a chunk position into the container's plot grid using the container origin and plot size.</p>
     */
    private boolean isOccupiedSablePlot(SubLevelContainer container, ChunkPos chunkPos) {
        if (!container.inBounds(chunkPos)) {
            return false;
        }

        int plotX = (chunkPos.x >> container.getLogPlotSize()) - container.getOrigin().x;
        int plotZ = (chunkPos.z >> container.getLogPlotSize()) - container.getOrigin().y;
        return container.getOccupancy().get(container.getIndex(plotX, plotZ));
    }

    /**
     * Requests the chunks needed by a stored Sable sub-level and asks Sable's holding map to process
     * pending loads.
     *
     * <p>This method is intentionally conservative: it queues the holding chunk identified by the
     * saved pointer, adds post-teleport tickets around the serialized bounds, synchronously requests
     * those chunks, and then calls Sable's holding-map processing once. That is enough for Sable's
     * normal loader when its own readiness checks pass.</p>
     *
     * <p>This does not guarantee that Sable will immediately expose a live {@link ServerSubLevel}.
     * Sable's own loading path also depends on holding chunk state and chunk readiness. Callers that
     * require an immediate live sub-level must verify the result with
     * {@link SableCompanion#getContaining(Level, Position)} and use the direct-load fallback when needed.</p>
     *
     * <p>A {@code null} pointer means the stored data came from runtime holding state rather than a
     * stable saved pointer. In that case there is no holding chunk to mark loaded, but the bounds can
     * still be ticketed before direct-load fallback is considered.</p>
     */
    private void forceLoadTeleportSubLevel(ServerLevel level, ServerSubLevelContainer container, SubLevelData data, GlobalSavedSubLevelPointer pointer) {
        var bounds = data.bounds();

        int minChunkX = Mth.floor(bounds.minX() - 1.0) >> 4;
        int minChunkZ = Mth.floor(bounds.minZ() - 1.0) >> 4;
        int maxChunkX = Mth.floor(bounds.maxX() + 1.0) >> 4;
        int maxChunkZ = Mth.floor(bounds.maxZ() + 1.0) >> 4;

        if (pointer != null) {
            container.getHoldingChunkMap().updateChunkStatus(pointer.chunkPos(), true);
        }

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                ChunkPos chunkPos = new ChunkPos(x, z);
                level.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, 0);
                level.getChunk(x, z);
            }
        }

        container.getHoldingChunkMap().processChanges();
    }

    /**
     * Stored Sable sub-level data plus the saved pointer that located it, if one exists.
     *
     * <p>The pointer may be {@code null} for data discovered from runtime holding state. Callers must
     * not assume that every stored sub-level currently has a stable saved pointer.</p>
     */
    private record StoredSubLevel(SubLevelData data, GlobalSavedSubLevelPointer pointer) {
    }

    /**
     * Converts an outgoing teleport frame from world space into source sub-level local space.
     *
     * <p>When an entity leaves a Sable sub-level, DimDoors needs the frame relative to the source
     * sub-level rather than the transformed world pose. Position, velocity, and rotation are all
     * inverse-transformed through the source pose.</p>
     *
     * <p>Sable reports sub-level velocity in blocks per second, while entity delta movement is in
     * blocks per tick. The inherited sub-level velocity is divided by 20 before being removed from
     * the entity's local velocity.</p>
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
     * Projects an incoming teleport frame into the target level.
     *
     * <p>The method first tries to project through a live target sub-level. If none is loaded, it
     * attempts to materialize the target plot using the rift's load position. If that still fails,
     * unloaded-rift projection attempts to recover a stored pose. Only when no Sable-specific path
     * applies does the method return the frame unchanged.</p>
     */
    @Override
    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        var subLevel = getTargetSubLevel(level, location, pos);

        if (subLevel == null) {
            Vec3 loadPos = getTargetSubLevelLoadPosition(level, location, pos);
            ensureSableSubLevelLoaded(level, loadPos);
            subLevel = getTargetSubLevel(level, location, pos);
        }

        if (subLevel == null) {
            TeleportFrame unloadedFrame = projectUnloadedRiftTeleportFrame(level, location, pos, angle, velocity);
            if (unloadedFrame != null) {
                return unloadedFrame;
            }

            validateTeleportDestination(level, pos);
            return new TeleportFrame(pos, angle, velocity);
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
     * Applies a known Sable pose to a local teleport frame.
     *
     * <p>The caller is responsible for supplying velocity already transformed into target world space,
     * because loaded and unloaded paths differ in how inherited Sable velocity is recovered.</p>
     */
    private TeleportFrame projectTeleportFrame(Pose3dc pose, Vec3 pos, Rotations angle, Vec3 worldVelocity) {
        Vec3 worldPos = pose.transformPosition(pos);
        Rotations worldAngle = transformAngle(pose, angle, false);

        return new TeleportFrame(worldPos, worldAngle, worldVelocity);
    }

    /**
     * Transforms an Euler rotation through a Sable pose without relying on direct Euler composition.
     *
     * <p>The method builds a forward/up basis from the input rotation, transforms those basis vectors
     * through the pose, then reconstructs an Euler angle from the transformed basis. This avoids many
     * of the discontinuities that can appear when composing yaw, pitch, and roll directly.</p>
     *
     * <p>When the transformed forward vector or projected up vector degenerates, the method falls back
     * to the safest available orientation rather than producing NaN rotations.</p>
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
     * Converts collision/history data into the tracked Sable sub-level's local space.
     *
     * <p>When an entity is being tracked by a Sable sub-level, its previous/current positions and
     * bounding box need to be interpreted in that sub-level's local coordinates. The bounding box is
     * rebuilt by transforming all eight corners and taking a new axis-aligned envelope in local space.</p>
     */
    @Override
    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        AABB currentBox = box;
        AABB previousBox = currentBox.move(previousPos.subtract(currentPos));

        var trackingSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(entity);
        if (trackingSubLevel == null) {
            return new AfterBlockData(encompass(previousBox, currentBox), previousBox, currentBox, previousPos, currentPos);
        }

        var pose = trackingSubLevel.logicalPose();

        previousPos = pose.transformPositionInverse(previousPos);
        currentPos = pose.transformPositionInverse(currentPos);

        previousBox = transformBoxInverse(pose, previousBox);
        currentBox = transformBoxInverse(pose, currentBox);
        box = encompass(previousBox, currentBox);

        return new AfterBlockData(box, previousBox, currentBox, previousPos, currentPos);
    }

    private AABB transformBoxInverse(Pose3dc pose, AABB box) {
        Vec3[] corners = new Vec3[] {
                new Vec3(box.minX, box.minY, box.minZ),
                new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ),
                new Vec3(box.minX, box.maxY, box.maxZ),
                new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ),
                new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.maxZ)
        };

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Vec3 corner : corners) {
            Vec3 localCorner = pose.transformPositionInverse(corner);

            minX = Math.min(minX, localCorner.x);
            minY = Math.min(minY, localCorner.y);
            minZ = Math.min(minZ, localCorner.z);
            maxX = Math.max(maxX, localCorner.x);
            maxY = Math.max(maxY, localCorner.y);
            maxZ = Math.max(maxZ, localCorner.z);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
