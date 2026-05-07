package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
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

public class ActiveSableHelper extends SableHelper {
    private static final Pattern SABLE_REGION_FILE_PATTERN = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.slvlr");
    private static Vector3d scratch = new Vector3d();

    @Override
    public Vec3 projectFrom(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos);
    }

    @Override
    public BlockPos projectFrom(Level level, BlockPos pos) {
        scratch.set(pos.getX(), pos.getY(), pos.getZ());
        SableCompanion.INSTANCE.projectOutOfSubLevel(level, scratch);
        return BlockPos.containing(scratch.x(), scratch.y(), scratch.z());
    }

    @Override
    public Vec3 projectTo(ServerLevel level, Vec3 pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(pos);
        }

        return pos;
    }

    @Override
    public void projectTo(ServerLevel level, Vector3d pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            subLevel.logicalPose().transformPositionInverse(pos);
        }
    }

    @Override
    public boolean isMissingSablePlotHolder(ServerLevel level, BlockPos pos) {
        var container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return false;
        }

        var chunkPos = new ChunkPos(pos);
        return container.inBounds(chunkPos) && container.getChunkHolder(chunkPos) == null;
    }

    @Override
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
        if (isMissingSablePlotHolder(level, BlockPos.containing(pos))) {
            throw new IllegalStateException("Teleport target " + pos + " in " + level.dimension().location() + " is inside Sable's plot grid, but no plot chunk holder is loaded there");
        }
    }

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

    @Override
    public TeleportFrame projectTeleportFrame(ServerLevel level, Vec3 pos, Rotations angle, Vec3 velocity) {
        return projectTeleportFrame(level, null, pos, angle, velocity);
    }

    @Override
    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

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

    private TeleportFrame projectTeleportFrame(Pose3dc pose, Vec3 pos, Rotations angle, Vec3 worldVelocity) {
        Vec3 worldPos = pose.transformPosition(pos);
        Rotations worldAngle = transformAngle(pose, angle, false);

        return new TeleportFrame(worldPos, worldAngle, worldVelocity);
    }

    private TeleportFrame projectUnloadedRiftTeleportFrame(ServerLevel level, Location targetLocation, Vec3 pos, Rotations angle, Vec3 velocity) {
        if (!isMissingSablePlotHolder(level, BlockPos.containing(pos))) {
            return null;
        }

        Location location = findRiftLocation(level, targetLocation, pos);
        if (location == null) {
            return null;
        }

        var registry = DimensionalRegistry.getRiftRegistry();

        Rift rift = registry.getRift(location);
        if (!(rift instanceof SableRiftData sableRift)) {
            return null;
        }

        UUID trackingPointId = sableRift.dimdoors$getSableTrackingPoint();
        Pose3dc pose = trackingPointId == null ? null : resolveTrackingPointPose(level, trackingPointId);
        if (pose == null) {
            pose = resolveUntrackedRiftPose(level, rift, location);
        }
        if (pose == null) {
            return null;
        }

        Vec3 worldVelocity = pose.transformNormal(velocity);
        return projectTeleportFrame(pose, pos, angle, worldVelocity);
    }

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

        return null;
    }

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
            return null;
        }

        UUID trackingPoint = SubLevelTrackingPointSavedData.getOrLoad(level).generateTrackingPoint(localRiftPos, serverSubLevel);
        sableRift.dimdoors$setSableTrackingPoint(trackingPoint);
        DimensionalRegistry.setDirty();

        return serverSubLevel.logicalPose();
    }

    private StoredSubLevel findStoredSubLevelContaining(ServerSubLevelContainer container, Vec3 pos) {
        ChunkPos targetChunk = new ChunkPos(BlockPos.containing(pos));
        int targetPlotX = (targetChunk.x >> container.getLogPlotSize()) - container.getOrigin().x;
        int targetPlotZ = (targetChunk.z >> container.getLogPlotSize()) - container.getOrigin().y;

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

                    if (!data.fullTag().contains("plot", Tag.TAG_COMPOUND)) {
                        continue;
                    }

                    var plotTag = data.fullTag().getCompound("plot");
                    if (plotTag.getInt("plot_x") == targetPlotX && plotTag.getInt("plot_z") == targetPlotZ) {
                        return new StoredSubLevel(data, new GlobalSavedSubLevelPointer(holdingChunkPos, pointer.storageIndex(), pointer.subLevelIndex()));
                    }
                }
            }
        }

        return null;
    }

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

    private record StoredSubLevel(SubLevelData data, GlobalSavedSubLevelPointer pointer) {
    }

    @Override
    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        var trackingSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(entity);
        if (trackingSubLevel == null) {
            return new TeleportFrame(pos, angle, velocity);
        }

        var sourceSubLevel = SableCompanion.INSTANCE.getContaining(level, sourcePos);
        if (sourceSubLevel == null || !sourceSubLevel.getUniqueId().equals(trackingSubLevel.getUniqueId())) {
            return new TeleportFrame(pos, angle, velocity);
        }

        var pose = trackingSubLevel.logicalPose();

        Vec3 localPos = pose.transformPositionInverse(pos);

        Vec3 inheritedVelocity = SableCompanion.INSTANCE
                .getVelocity(level, trackingSubLevel, localPos)
                .scale(1.0 / 20.0);
        Vec3 localVelocity = pose.transformNormalInverse(velocity.subtract(inheritedVelocity));

        Rotations localAngle = transformAngle(pose, angle, true);

        return new TeleportFrame(localPos, localAngle, localVelocity);
    }

    private Rotations transformAngle(Pose3dc pose, Rotations angle, boolean inverse) {
        var rotator = TransformationMatrix3d.builder().rotate(angle).build();
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
