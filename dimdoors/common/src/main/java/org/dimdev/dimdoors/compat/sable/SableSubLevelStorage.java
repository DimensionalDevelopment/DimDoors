package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkMapAccessor;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds unloaded Sable sub-levels by the plot they occupy.
 *
 * <p>Sable indexes stored sub-levels by id and by saved pointer, and its occupancy grid says whether
 * a plot is taken but not which sub-level took it. Nothing maps a world position back to storage, so
 * this searches for one: runtime holding entries first, then the holding-region files on disk.</p>
 *
 * <p>That search is expensive and exists only for rifts made before Sable integration, which carry no
 * tracking point. It runs once per such rift, writes the tracking point, and is never needed for that
 * rift again — {@link SableLevelSpaceHelper} handles every other case from the tracking point alone.
 * When no untracked rifts remain in the wild, this class can be deleted.</p>
 */
final class SableSubLevelStorage {
    private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.slvlr");

    private SableSubLevelStorage() {
    }

    /**
     * Finds the rift's sub-level in storage, loads it, and records a tracking point for it.
     *
     * @return the new tracking point id, or {@code null} if no stored sub-level was found or it failed
     * to go live
     */
    static UUID trackRiftFromStorage(ServerLevel level, Rift rift, Location location) {
        if (!(rift instanceof SableRiftData sableRift)) {
            return null;
        }

        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }

        Vec3 riftPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
        StoredSubLevel storedSubLevel = findContaining(container, riftPos);
        if (storedSubLevel == null) {
            DimensionalDoors.LOGGER.warn("No stored Sable sub-level found for untracked rift at {}", location);
            return null;
        }

        UUID subLevelId = storedSubLevel.data().uuid();
        SubLevelHoldingChunkMap holdingChunkMap = container.getHoldingChunkMap();

        // A holding entry is loaded directly. Its pointer names where the sub-level was last written
        // to disk, not the holding chunk it sits in, so snatching by it can look in the wrong chunk.
        // Only a pointer recovered from a region file describes the chunk it was read from.
        HoldingSubLevel holdingSubLevel = holdingChunkMap.getHoldingSubLevel(subLevelId);

        if (holdingSubLevel != null) {
            if (!snatchAndLoadFromOwningChunk(holdingChunkMap, subLevelId)) {
                loadChain(holdingChunkMap, holdingSubLevel);
            }
        } else if (storedSubLevel.pointer() != null) {
            holdingChunkMap.snatchAndLoad(storedSubLevel.pointer(), subLevelId);
        } else {
            return null;
        }

        if (!(container.getSubLevel(subLevelId) instanceof ServerSubLevel serverSubLevel)) {
            DimensionalDoors.LOGGER.warn("Sable sub-level {} for untracked rift at {} did not become live", subLevelId, location);
            return null;
        }

        var trackingData = SubLevelTrackingPointSavedData.getOrLoad(level);
        UUID trackingPoint = trackingData.generateTrackingPoint(riftPos, serverSubLevel);
        sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
        RiftRegistry.getInstance().setDirty();

        SableLevelSpaceHelper.logRecordedTrackingPoint(trackingData, trackingPoint, location, "a previously untracked");
        return trackingPoint;
    }

    /**
     * Loads a holding entry through Sable's own snatch path, locating the holding chunk by searching for
     * the one that actually contains the entry instead of trusting the entry's pointer.
     *
     * <p>{@code snatchAndLoad} takes its holding chunk from the pointer, but Sable stamps an entry
     * unloaded at runtime with the sub-level's last serialization pointer, which names where it was last
     * written to disk. Snatching is still what we want, because it removes the entry from the chunk and
     * marks that chunk dirty; only the chunk it resolves from the pointer is wrong.</p>
     *
     * @return {@code true} if an owning chunk was found and the entry was handed to Sable
     */
    static boolean snatchAndLoadFromOwningChunk(SubLevelHoldingChunkMap holdingChunkMap, UUID subLevelId) {
        var loadedChunks = ((SubLevelHoldingChunkMapAccessor) holdingChunkMap).dimdoors$getLoadedHoldingChunks();

        List<ChunkPos> owningChunks = new ArrayList<>();
        GlobalSavedSubLevelPointer stampedPointer = null;

        for (SubLevelHoldingChunk holdingChunk : loadedChunks.values()) {
            for (HoldingSubLevel candidate : holdingChunk.getLoadedHoldingSubLevels()) {
                if (candidate.data().uuid().equals(subLevelId)) {
                    owningChunks.add(holdingChunk.getChunkPos());
                    stampedPointer = candidate.pointer();
                }
            }
        }

        if (owningChunks.isEmpty()) {
            DimensionalDoors.LOGGER.warn("Sable sub-level {} has a holding entry but no loaded holding chunk claims it", subLevelId);
            return false;
        }

        DimensionalDoors.LOGGER.info("Sable sub-level {} is held by chunk(s) {} while its entry points at {}", subLevelId, owningChunks, stampedPointer);

        holdingChunkMap.snatchAndLoad(new GlobalSavedSubLevelPointer(owningChunks.get(0), (short) 0, (short) 0), subLevelId);
        return true;
    }

    /**
     * Loads a holding sub-level and its chain partners, for entries that carry no pointer.
     *
     * <p>The partners are required, not incidental: Sable discards any queued sub-level whose chain
     * partner has gone missing, so loading one alone strands the rest.</p>
     */
    static void loadChain(SubLevelHoldingChunkMap holdingChunkMap, HoldingSubLevel root) {
        for (UUID dependency : root.data().dependencies()) {
            HoldingSubLevel partner = holdingChunkMap.getHoldingSubLevel(dependency);
            if (partner != null) {
                holdingChunkMap.loadHoldingSubLevel(partner);
            }
        }

        holdingChunkMap.loadHoldingSubLevel(root);
    }

    /**
     * Checks whether a chunk belongs to an occupied Sable plot, converting it into plot-grid
     * coordinates first since occupancy is indexed by plot, not chunk.
     */
    static boolean isOccupiedPlot(SubLevelContainer container, ChunkPos chunkPos) {
        if (!container.inBounds(chunkPos)) {
            return false;
        }

        int plotX = (chunkPos.x >> container.getLogPlotSize()) - container.getOrigin().x;
        int plotZ = (chunkPos.z >> container.getLogPlotSize()) - container.getOrigin().y;
        return container.getOccupancy().get(container.getIndex(plotX, plotZ));
    }

    /**
     * Finds stored data for the plot containing {@code pos}, searching runtime holding entries before
     * falling back to region files on disk.
     */
    private static StoredSubLevel findContaining(ServerSubLevelContainer container, Vec3 pos) {
        ChunkPos targetChunk = new ChunkPos(BlockPos.containing(pos));
        if (!isOccupiedPlot(container, targetChunk)) {
            return null;
        }

        int plotX = (targetChunk.x >> container.getLogPlotSize()) - container.getOrigin().x;
        int plotZ = (targetChunk.z >> container.getLogPlotSize()) - container.getOrigin().y;

        StoredSubLevel held = findInHoldingMap(container, plotX, plotZ);
        if (held != null) {
            return held;
        }

        Path folder = container.getHoldingChunkMap().getStorage().getFolder();
        if (!Files.isDirectory(folder)) {
            return null;
        }

        try (var paths = Files.newDirectoryStream(folder, "*.slvlr")) {
            for (Path path : paths) {
                Matcher matcher = REGION_FILE.matcher(path.getFileName().toString());
                if (!matcher.matches()) {
                    continue;
                }

                StoredSubLevel found = findInRegion(container, plotX, plotZ,
                        Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                if (found != null) {
                    return found;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return null;
    }

    /**
     * Searches Sable's runtime holding map, which may hold newer data than storage for a recently
     * moved or unloaded sub-level.
     */
    private static StoredSubLevel findInHoldingMap(ServerSubLevelContainer container, int plotX, int plotZ) {
        var holdingSubLevels = ((SubLevelHoldingChunkMapAccessor) container.getHoldingChunkMap()).dimdoors$getAllHoldingSubLevels();

        for (HoldingSubLevel holdingSubLevel : holdingSubLevels.values()) {
            if (isInPlot(holdingSubLevel.data(), plotX, plotZ)) {
                return new StoredSubLevel(holdingSubLevel.data(), holdingSubLevel.pointer());
            }
        }

        return null;
    }

    /**
     * Searches one holding-region file, resolving the saved pointers in each of its 32 by 32 holding
     * chunks until the referenced data matches the target plot.
     */
    private static StoredSubLevel findInRegion(ServerSubLevelContainer container, int plotX, int plotZ, int regionX, int regionZ) {
        var storage = container.getHoldingChunkMap().getStorage();

        for (int localX = 0; localX < 32; localX++) {
            for (int localZ = 0; localZ < 32; localZ++) {
                ChunkPos holdingChunkPos = new ChunkPos((regionX << 5) + localX, (regionZ << 5) + localZ);
                var holdingChunk = storage.attemptLoadHoldingChunk(holdingChunkPos);
                if (holdingChunk == null) {
                    continue;
                }

                for (SavedSubLevelPointer pointer : holdingChunk.getSubLevelPointers()) {
                    SubLevelData data = storage.attemptLoadSubLevel(holdingChunkPos, pointer);
                    if (data != null && isInPlot(data, plotX, plotZ)) {
                        return new StoredSubLevel(data, new GlobalSavedSubLevelPointer(holdingChunkPos, pointer.storageIndex(), pointer.subLevelIndex()));
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks whether serialized data belongs to the requested plot, reading Sable's {@code plot} tag
     * rather than inferring from bounds, which shift for moved or rotated sub-levels.
     */
    private static boolean isInPlot(SubLevelData data, int plotX, int plotZ) {
        if (!data.fullTag().contains("plot", Tag.TAG_COMPOUND)) {
            return false;
        }

        var plotTag = data.fullTag().getCompound("plot");
        return plotTag.getInt("plot_x") == plotX && plotTag.getInt("plot_z") == plotZ;
    }

    /**
     * Stored sub-level data with the pointer that located it. The pointer is {@code null} for data
     * found in runtime holding state.
     */
    private record StoredSubLevel(SubLevelData data, GlobalSavedSubLevelPointer pointer) { }
}
