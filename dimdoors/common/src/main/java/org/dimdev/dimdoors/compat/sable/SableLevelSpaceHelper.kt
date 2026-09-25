package org.dimdev.dimdoors.compat.sable

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver
import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.companion.SubLevelAccess
import dev.ryanhcode.sable.companion.math.BoundingBox3d
import dev.ryanhcode.sable.platform.SableEventPlatform
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import dev.ryanhcode.sable.sublevel.SubLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.util.BlockPosUtil
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.math.inverse
import org.dimdev.dimdoors.api.util.math.transform
import org.dimdev.dimdoors.rift.registry.Rift
import org.dimdev.dimdoors.rift.registry.RiftRegistry
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import org.dimdev.dimdoors.util.LevelSpaceHelper
import org.dimdev.dimdoors.util.LevelSpaceHelper.AfterBlockData
import org.dimdev.dimdoors.util.LevelSpaceHelper.TeleportFrame
import org.joml.Matrix4d

object SableLevelSpaceHelper : LevelSpaceHelper() {
    @JvmStatic
    fun init() {
        LevelSpaceHelper.INSTANCE = this
        SableEventPlatform.INSTANCE.onSubLevelContainerReady { level, container ->
            if (level is ServerLevel) container.addObserver(object : SubLevelObserver {
                override fun onSubLevelAdded(subLevel: SubLevel) = trackRiftsIn(level, subLevel)
            })
        }
    }

    fun isUnavailableNow(level: ServerLevel, pos: BlockPos): Boolean {
        val container = ServerSubLevelContainer.getContainer(level) ?: return false
        val chunkPos = ChunkPos(pos)
        return SableSubLevels.isOccupiedPlot(container, chunkPos) && container.getChunkHolder(chunkPos) == null
    }

    fun isUnavailable(level: ServerLevel, pos: BlockPos): Boolean {
        ensureLoaded(level, pos)
        return isUnavailableNow(level, pos)
    }

    fun track(level: ServerLevel, rift: Rift) {
        val id = SableCompanion.INSTANCE.getContaining(level, rift.location.blockPos)?.uniqueId
        if (rift.levelSpaceId == id) return
        rift.levelSpaceId = id
        RiftRegistry.instance.setDirty()
    }

    override fun onRiftAdded(rift: Rift) = track(rift.location.world, rift)

    override fun getBlockEntity(level: ServerLevel, pos: BlockPos): BlockEntity? {
        ensureLoaded(level, pos)
        return level.getBlockEntity(pos)
    }

    override fun getBlockState(level: ServerLevel, pos: BlockPos): BlockState {
        ensureLoaded(level, pos)
        return level.getBlockState(pos)
    }

    override fun validateTeleportDestination(level: ServerLevel, pos: Vec3) {
        check(!isUnavailable(level, BlockPos.containing(pos))) { "Teleport target $pos in ${level.dimension().location()} is inside Sable's plot grid, but no plot chunk holder is loaded there" }
    }

    override fun prepareRiftCreation(level: ServerLevel, pos: BlockPos): Boolean {
        val container = ServerSubLevelContainer.getContainer(level) ?: return true
        val chunkPos = ChunkPos(pos)
        if (!container.inBounds(chunkPos) || container.getChunkHolder(chunkPos) != null) return true

        ensureLoaded(level, pos)
        if (container.getChunkHolder(chunkPos) != null) return true

        val plot = (SableCompanion.INSTANCE.getContaining(level, pos) as? ServerSubLevel)?.plot ?: return false
        val localChunkPos = plot.toLocal(chunkPos)
        if (plot.getChunkHolder(localChunkPos) == null) plot.newEmptyChunk(chunkPos)
        return plot.getChunkHolder(localChunkPos) != null
    }

    override fun sourceTeleportFrame(level: ServerLevel, sourcePos: BlockPos, entity: Entity?, pos: Vec3, angle: Rotations, velocity: Vec3): TeleportFrame {
        val subLevel = SableCompanion.INSTANCE.getContaining(level, sourcePos) ?: return TeleportFrame(pos, angle, velocity)
        val pose = subLevel.logicalPose()
        val localPos = pose.transformPositionInverse(pos)

        return TeleportFrame(
            localPos,
            pose.bakeIntoMatrix(Matrix4d()).inverse().transform(angle),
            pose.transformNormalInverse(velocity.subtract(inheritedVelocity(level, subLevel, localPos)))
        )
    }

    override fun projectTeleportFrame(level: ServerLevel, location: Location?, pos: Vec3, angle: Rotations, velocity: Vec3): TeleportFrame {
        val probe = location?.takeIf { it.worldId == level.dimension() }?.blockPos ?: BlockPos.containing(pos)
        ensureLoaded(level, probe)

        val subLevel = SableCompanion.INSTANCE.getContaining(level, probe) ?: SableCompanion.INSTANCE.getContaining(level, pos)
        if (subLevel == null) {
            validateTeleportDestination(level, pos)
            return TeleportFrame(pos, angle, velocity)
        }

        if (subLevel is ServerSubLevel) riftNear(level, probe)?.takeIf { it.levelSpaceId == null }?.let { track(level, it) }

        val pose = subLevel.logicalPose()
        return TeleportFrame(
            pose.transformPosition(pos),
            pose.bakeIntoMatrix(Matrix4d()).transform(angle),
            pose.transformNormal(velocity).add(inheritedVelocity(level, subLevel, pos))
        )
    }

    override fun getAfterBlockData(entity: Entity, box: AABB, previousPos: Vec3, currentPos: Vec3): AfterBlockData {
        val pose = SableCompanion.INSTANCE.getTrackingSubLevel(entity)?.logicalPose() ?: return AfterBlockData(box, previousPos, currentPos)
        return AfterBlockData(
            BoundingBox3d(box).transformInverse(pose, BoundingBox3d()).toMojang(),
            pose.transformPositionInverse(previousPos),
            pose.transformPositionInverse(currentPos)
        )
    }

    private fun ensureLoaded(level: ServerLevel, pos: BlockPos) {
        if (isUnavailableNow(level, pos)) riftNear(level, pos)?.let { SableSubLevels.resolve(level, it) }
    }

    private fun riftNear(level: ServerLevel, pos: BlockPos): Rift? {
        val registry = RiftRegistry.instance
        return BlockPosUtil.nearbyVertical(pos) { candidate ->
            Location.ofWorld(level, candidate).takeIf(registry::isRiftAt)?.let(registry::getRift)
        }
    }

    private fun trackRiftsIn(level: ServerLevel, subLevel: SubLevel) {
        if (!level.server.isReady) return
        val registry = SubSystem.getInstance(level.server, SubsystemTypes.RIFT.value()) ?: return

        for (rift in registry.rifts) {
            if (rift.location.worldId != level.dimension() || rift.levelSpaceId == subLevel.uniqueId) continue
            if (!subLevel.plot.contains(ChunkPos(rift.location.blockPos))) continue
            rift.levelSpaceId = subLevel.uniqueId
            registry.setDirty()
        }
    }

    private fun inheritedVelocity(level: ServerLevel, subLevel: SubLevelAccess, pos: Vec3): Vec3 =
        SableCompanion.INSTANCE.getVelocity(level, subLevel, pos).scale(1.0 / 20.0)
}
