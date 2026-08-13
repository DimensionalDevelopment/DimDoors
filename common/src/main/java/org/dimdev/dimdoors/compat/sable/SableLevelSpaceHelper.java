package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
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
import org.dimdev.dimdoors.api.util.BlockPosUtil;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkAccessor;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkMapAccessor;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.RotationUtil;
import org.dimdev.dimdoors.util.LevelSpaceHelper;
import org.joml.Vector3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sable implementation of {@link LevelSpaceHelper}.
 *
 * <p>This class integrates DimDoors' rift, teleportation, collision, and block-access systems with
 * Sable's movable level spaces. Sable sub-levels have their own coordinates, pose, velocity, and
 * storage lifecycle, while DimDoors normally operates in Minecraft world space.</p>
 *
 * <p>The helper is responsible for:</p>
 *
 * <ol>
 *     <li>Transforming positions, rotations, velocities, and collision data between Sable level
 *     space and world space.</li>
 *     <li>Resolving rift targets inside loaded, unloaded, or partially loaded Sable plots.</li>
 *     <li>Materializing stored Sable sub-levels when DimDoors requires immediate access to a live
 *     {@link ServerSubLevel} or plot holder.</li>
 * </ol>
 *
 * <h2>Loaded and stored sub-levels</h2>
 *
 * <p>A Sable plot may remain occupied while its live {@link ServerSubLevel} is unloaded. In that
 * state, Sable retains serialized {@link SubLevelData} in holding storage. DimDoors may still need
 * to teleport into the plot, create rifts there, or resolve tracking points, so this helper can
 * locate the stored data and recover the pose required for projection.</p>
 *
 * <h2>Teleport frame flow</h2>
 *
 * <p>Teleport frame conversion is split between the source and destination:</p>
 *
 * <ul>
 *     <li>{@link #sourceTeleportFrame(ServerLevel, BlockPos, Entity, Vec3, Rotations, Vec3)}
 *     transforms a frame leaving a Sable sub-level from world space into the source level
 *     space.</li>
 *     <li>{@link #projectTeleportFrame(ServerLevel, Location, Vec3, Rotations, Vec3)} transforms
 *     an incoming frame from destination level space into the target world-space pose.</li>
 * </ul>
 *
 * <p>Destination projection prefers a live sub-level. If the target plot is occupied but unloaded,
 * the helper first asks Sable's holding system to materialize the stored sub-level. If that does
 * not produce a live sub-level immediately, the stored data is loaded directly as a fallback.
 * Runtime holding entries are then removed to prevent Sable from loading the same data again on
 * its next processing pass.</p>
 *
 * <h2>Persistent storage safety</h2>
 *
 * <p>Runtime cleanup must never remove saved pointers from
 * {@code SubLevelHoldingChunk#getSubLevelPointers()}. These pointers are persistent storage
 * metadata, and removing them can orphan stored sub-levels. Direct-load cleanup therefore removes
 * only runtime entries such as {@code allHoldingSubLevels} and {@code loadedHoldingSubLevels}.</p>
 */
public class SableLevelSpaceHelper extends LevelSpaceHelper {
    private static final Pattern SABLE_REGION_FILE_PATTERN = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.slvlr");
    private final Vector3d scratch = new Vector3d();

    /**
     * Checks whether {@code pos} belongs to an occupied Sable plot whose live holder is unavailable.
     *
     * <p>The helper first attempts to materialize the containing sub-level. If the plot remains
     * occupied without a live holder afterward, its level space is considered unavailable.</p>
     */
    public boolean isLevelSpaceUnavailable(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, pos);
        var container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return false;
        }

        var chunkPos = new ChunkPos(pos);
        return isOccupiedSablePlot(container, chunkPos) && container.getChunkHolder(chunkPos) == null;
    }

    /**
     * Returns the block entity at {@code pos} after giving Sable an opportunity to materialize the
     * containing plot.
     *
     * <p>DimDoors may request block entities at Sable plot coordinates before the corresponding live
     * holder exists. Loading the sub-level first ensures the vanilla lookup sees the expected block
     * state.</p>
     */
    @Override
    public BlockEntity getBlockEntity(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, pos);
        return level.getBlockEntity(pos);
    }

    /**
     * Attempts to ensure that the Sable sub-level containing {@code pos} is loaded.
     *
     * @param level the server level containing the position
     * @param pos the block position to resolve
     */
    private void ensureSableSubLevelLoaded(ServerLevel level, BlockPos pos) {
        ensureSableSubLevelLoaded(level, Vec3.atCenterOf(pos));
    }

    /**
     * Ensures that an occupied Sable plot containing {@code pos} has an accessible live holder or
     * sub-level.
     *
     * <p>Non-Sable levels, positions outside the plot grid, and unoccupied plots require no special
     * handling. Occupied plots without a live holder are resolved through Sable's holding storage
     * and, when necessary, materialized from stored sub-level data.</p>
     *
     * @return {@code true} if no special loading is required or a live holder or sub-level is
     * available; {@code false} if an occupied plot cannot be materialized
     */
    private boolean ensureSableSubLevelLoaded(ServerLevel level, Vec3 pos) {
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
     * Validates that a teleport destination does not resolve to an unavailable Sable level space.
     *
     * <p>This is checked before falling back to an unchanged world-space teleport frame. Treating an
     * occupied but unavailable Sable plot as ordinary world space would place the entity into
     * invalid plot coordinates.</p>
     */
    @Override
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
        if (isLevelSpaceUnavailable(level, BlockPos.containing(pos))) {
            throw new IllegalStateException("Teleport target " + pos + " in " + level.dimension().location() + " is inside Sable's plot grid, but no plot chunk holder is loaded there");
        }
    }

    /**
     * Prepares the Sable level space containing {@code pos} for rift creation or modification.
     *
     * <p>Positions outside Sable's plot grid require no preparation. For positions inside the grid,
     * this method ensures that an occupied sub-level is available and that its plot contains a live
     * chunk holder for the chunk containing the rift.</p>
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
     * <p>Tracking points preserve the current spatial state of a rift inside a movable Sable
     * sub-level, allowing its pose to be recovered after the containing sub-level is unloaded.
     * Existing tracking points are replaced so moved or relocated rifts do not retain stale pose
     * references.</p>
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
                RiftRegistry.getInstance().setDirty();
            }
            return;
        }

        Vec3 localPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
        var subLevel = SableCompanion.INSTANCE.getContaining(level, localPos);
        if (!(subLevel instanceof ServerSubLevel serverSubLevel)) {
            if (previousTrackingPoint != null) {
                RiftRegistry.getInstance().setDirty();
            }
            return;
        }

        UUID trackingPoint = trackingData.generateTrackingPoint(localPos, serverSubLevel);
        sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
        RiftRegistry.getInstance().setDirty();
    }

    /**
     * Removes the Sable tracking point associated with a DimDoors rift.
     *
     * <p>This is called when the rift no longer requires Sable pose tracking.</p>
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
        RiftRegistry.getInstance().setDirty();
    }

    /**
     * Resolves the loaded Sable level space that should receive a projected teleport frame.
     *
     * <p>When a target {@link Location} is known, the lookup uses the rift block rather than the
     * projected entity position. The entity position may be fractionally offset from the rift or
     * already affected by movement, while the rift position provides a stable probe into the target
     * plot.</p>
     *
     * <p>If that probe finds nothing and differs from the original projected position, the original
     * position is checked as a fallback.</p>
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
     * Chooses the position used to locate or load the target Sable level space.
     *
     * <p>If the target rift belongs to the destination level, its block position is used as the
     * stable load probe. Otherwise the projected entity position is used.</p>
     */
    private Vec3 getTargetSubLevelLoadPosition(ServerLevel level, Location location, Vec3 pos) {
        return location != null && location.world.equals(level.dimension())
                ? Vec3.upFromBottomCenterOf(location.pos, 0.0)
                : pos;
    }

    /**
     * Projects a teleport frame through an occupied Sable plot whose live level space is unavailable.
     *
     * <p>This fallback is used when ordinary target resolution fails. It attempts to recover the
     * target pose without requiring the corresponding {@link ServerSubLevel} to already be live.</p>
     *
     * <p>Pose resolution proceeds in this order:</p>
     *
     * <ol>
     *     <li>Resolve the rift's saved Sable tracking point, if present.</li>
     *     <li>Locate the stored sub-level containing an otherwise untracked rift.</li>
     *     <li>Locate the stored sub-level containing the target load position.</li>
     * </ol>
     *
     * <p>Once a pose is found, the frame is transformed through the same projection path used for
     * loaded Sable level spaces.</p>
     */
    private TeleportFrame projectUnloadedRiftTeleportFrame(ServerLevel level, Location targetLocation, Vec3 pos, Rotations angle, Vec3 velocity) {
        Vec3 loadPos = getTargetSubLevelLoadPosition(level, targetLocation, pos);

        if (!isLevelSpaceUnavailable(level, BlockPos.containing(loadPos))) {
            return null;
        }

        Pose3dc pose = null;

        Location location = findRiftLocation(level, targetLocation, pos);
        if (location != null) {
            Rift rift = RiftRegistry.getInstance().getRift(location);

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
     * Attempts to identify the DimDoors rift associated with a projected destination frame.
     *
     * <p>The explicit target location is preferred when it belongs to the destination level and
     * still contains a registered rift. Otherwise the projected block position and nearby vertical
     * positions are checked to tolerate small offsets around the rift block.</p>
     */
    private Location findRiftLocation(ServerLevel level, Location targetLocation, Vec3 pos) {
        var registry = RiftRegistry.getInstance();

        if (targetLocation != null && targetLocation.world.equals(level.dimension()) && registry.isRiftAt(targetLocation)) {
            return targetLocation;
        }

        BlockPos blockPos = BlockPos.containing(pos);

        return BlockPosUtil.nearbyVertical(blockPos, blockPos1 -> {
            Location location = Location.ofWorld(level, blockPos1);
            if (!registry.isRiftAt(location)) {
                return null;
            }
            return location;
        });
    }

    /**
     * Resolves a pose from a saved Sable tracking point.
     *
     * <p>A tracking point may refer to a live sub-level, a runtime holding entry, or persisted
     * sub-level data. A live sub-level provides the current logical pose. Stored data is first
     * processed through Sable's normal loading path and then used directly as a fallback if no live
     * {@link ServerSubLevel} becomes available.</p>
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
     * Resolves the pose of a Sable rift that does not yet have a tracking point and initializes
     * tracking when possible.
     *
     * <p>The rift position is used to locate the containing stored sub-level. If a live sub-level can
     * be materialized, a tracking point is created so later unloaded projections can avoid repeating
     * the storage search.</p>
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
        RiftRegistry.getInstance().setDirty();

        return serverSubLevel.logicalPose();
    }

    /**
     * Resolves a pose from the stored Sable sub-level containing {@code pos}.
     *
     * <p>This path is used when no rift tracking information is available. It first attempts to
     * materialize the stored sub-level. If a live sub-level becomes available, its logical pose is
     * returned; otherwise the serialized pose is used as a fallback.</p>
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
     * Removes a directly loaded sub-level from Sable's runtime holding queues.
     *
     * <p>This is used only after the helper directly calls
     * {@link SubLevelSerializer#fullyLoad(ServerLevel, SubLevelData)}. That call creates a live
     * {@link ServerSubLevel} immediately, but Sable's holding map may still contain the same
     * {@link HoldingSubLevel}. Leaving it queued can cause a later {@code processChanges()} pass to
     * load the sub-level again and fail with a duplicate plot allocation.</p>
     *
     * <p>The search scans every loaded holding chunk rather than only the chunk named by the saved
     * pointer. Moved or partially serialized sub-levels may temporarily have a {@code null} pointer
     * or one that no longer matches the in-memory holding chunk containing the queued entry.</p>
     *
     * <p>Only runtime entries are removed. Saved pointers in
     * {@code SubLevelHoldingChunk#getSubLevelPointers()} are persistent storage metadata and must
     * remain intact or the stored sub-level may become orphaned.</p>
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
     * <p>This path is used when a position belongs to an occupied Sable plot but no live holder or
     * {@link ServerSubLevel} is available. Runtime holding entries are searched first, followed by
     * persisted holding-region files, for stored sub-level data assigned to the target plot.</p>
     *
     * <p>After a match is found, the helper first requests loading through
     * {@link #forceLoadTeleportSubLevel(ServerLevel, ServerSubLevelContainer, SubLevelData, GlobalSavedSubLevelPointer)}.
     * If Sable still does not expose a live sub-level immediately, the stored
     * {@link SubLevelData} is loaded directly through
     * {@link SubLevelSerializer#fullyLoad(ServerLevel, SubLevelData)}.</p>
     *
     * <p>Direct loading is followed by runtime-only holding cleanup so Sable does not process the
     * same queued entry again on its next pass. This is particularly important for moved sub-levels,
     * whose saved pointer may be stale or temporarily {@code null}.</p>
     *
     * <p>Saved holding pointers are never removed from persistent storage.</p>
     *
     * @return the matching stored sub-level, or {@code null} if none can be found
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
     * Finds stored Sable sub-level data assigned to the occupied plot containing {@code pos}.
     *
     * <p>The target chunk is converted to Sable plot coordinates. Runtime holding entries are
     * searched first, followed by holding-region files on disk. The disk fallback allows DimDoors to
     * recover stored data even when Sable has not loaded the relevant holding chunk into memory.</p>
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
     * Searches Sable's runtime holding map for sub-level data assigned to the target plot.
     *
     * <p>This is preferred over disk scanning because runtime holding state may contain newer data
     * for a recently moved or unloaded sub-level that has not yet been flushed to region storage.</p>
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
     * Searches one Sable holding-region file for sub-level data assigned to the target plot.
     *
     * <p>Each region contains up to 32 by 32 holding chunks. Their saved sub-level pointers are
     * resolved until a stored sub-level whose serialized plot tag matches
     * {@code targetPlotX,targetPlotZ} is found.</p>
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
     * Checks whether serialized sub-level data is assigned to the requested Sable plot.
     *
     * <p>The plot coordinate is read directly from Sable's serialized {@code plot} tag rather than
     * inferred from bounds, because moved or rotated sub-levels may span or shift across multiple
     * chunks.</p>
     */
    private boolean isStoredInPlot(SubLevelData data, int targetPlotX, int targetPlotZ) {
        if (!data.fullTag().contains("plot", Tag.TAG_COMPOUND)) {
            return false;
        }

        var plotTag = data.fullTag().getCompound("plot");
        return plotTag.getInt("plot_x") == targetPlotX && plotTag.getInt("plot_z") == targetPlotZ;
    }

    /**
     * Checks whether a chunk belongs to an occupied Sable plot.
     *
     * <p>Sable stores occupancy in plot coordinates rather than vanilla chunk coordinates. The
     * supplied chunk position is converted into the container's plot grid using its origin and plot
     * size before the occupancy bit is queried.</p>
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
     * Requests the chunks needed by a stored Sable sub-level and processes pending holding loads.
     *
     * <p>The method queues the holding chunk identified by the saved pointer, adds post-teleport
     * tickets around the serialized bounds, synchronously requests those chunks, and then runs one
     * holding-map processing pass. This gives Sable's normal loader the opportunity to materialize
     * the sub-level when its readiness checks pass.</p>
     *
     * <p>A live {@link ServerSubLevel} is not guaranteed immediately because Sable's loader also
     * depends on holding state and chunk readiness. Callers requiring an immediate live sub-level
     * must verify the result with {@link SableCompanion#getContaining(Level, Position)} and use the
     * direct-load fallback when necessary.</p>
     *
     * <p>A {@code null} pointer indicates data discovered only in runtime holding state. In that case
     * there is no saved holding chunk to mark as loaded, but the serialized bounds can still be
     * ticketed before direct loading is considered.</p>
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
     * Stored Sable sub-level data together with the saved pointer that located it, when one exists.
     *
     * <p>The pointer may be {@code null} when the data came from runtime holding state, so callers
     * must not assume every stored sub-level has a stable saved pointer.</p>
     */
    private record StoredSubLevel(SubLevelData data, GlobalSavedSubLevelPointer pointer) {
    }

    /**
     * Converts an outgoing teleport frame from world space into the source Sable level space.
     *
     * <p>When an entity leaves a Sable sub-level, DimDoors needs the frame relative to that
     * sub-level rather than its transformed world pose. Position, velocity, and rotation are
     * therefore inverse-transformed through the source pose.</p>
     *
     * <p>Sable reports sub-level velocity in blocks per second while entity delta movement is in
     * blocks per tick. The inherited sub-level velocity is divided by 20 before it is removed from
     * the entity's world-space velocity.</p>
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
     * Projects an incoming teleport frame into the target world space.
     *
     * <p>The method first attempts projection through a live target sub-level. If none is available,
     * it tries to materialize the target plot using the selected load position. If that still fails,
     * unloaded-rift projection attempts to recover a stored pose. The frame is returned unchanged
     * only when no Sable-specific projection applies.</p>
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
     * Transforms an Euler rotation through a Sable pose without directly composing Euler angles.
     *
     * <p>The input rotation is converted into forward and up basis vectors, those vectors are
     * transformed through the pose, and an Euler rotation is reconstructed from the resulting basis.
     * This avoids many discontinuities associated with direct yaw, pitch, and roll composition.</p>
     *
     * <p>If the transformed forward vector or projected up vector degenerates, the method falls back
     * to the safest available orientation instead of producing invalid rotations.</p>
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
     * Converts collision and movement history into the tracked Sable level space.
     *
     * <p>When an entity is tracked by a Sable sub-level, its previous and current positions and its
     * bounding box must be interpreted in that sub-level's coordinates. The bounding box is rebuilt
     * by inverse-transforming all eight corners and taking their axis-aligned envelope in Sable level
     * space.</p>
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

        box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        return new AfterBlockData(box, previousPos, currentPos);
    }
}
