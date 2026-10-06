package org.dimdev.dimdoors.compat.sable

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelStorage
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.LevelSpaceRegistry
import java.io.IOException
import java.nio.file.Files
import java.util.*

internal object SableSubLevels {
    private val REGION_FILE = Regex("""r\.(-?\d+)\.(-?\d+)\.slvlr""")

    fun resolve(level: ServerLevel, id: UUID, location: Location): ServerSubLevel? {
        val container = ServerSubLevelContainer.getContainer(level) ?: return null
        LevelSpaceRegistry.instance.get(id)?.let { space -> load(container, space, null)?.let { return it } }

        val stored = findStored(container, ChunkPos(location.blockPos)) ?: return null
        return load(container, stored.id, stored.pointer)?.also { SableLevelSpaceHelper.track(level, id, location) }
    }

    fun isOccupiedPlot(container: SubLevelContainer, chunkPos: ChunkPos): Boolean {
        if (!container.inBounds(chunkPos)) return false
        val (plotX, plotZ) = plotCoords(container, chunkPos)
        return container.occupancy.get(container.getIndex(plotX, plotZ))
    }

    private fun load(container: ServerSubLevelContainer, id: UUID, pointer: GlobalSavedSubLevelPointer?): ServerSubLevel? {
        container.getSubLevel(id)?.let { return it as? ServerSubLevel }

        val holdingMap = container.holdingChunkMap
        when {
            holdingMap.getHoldingSubLevel(id) != null -> snatchFromOwningChunk(holdingMap, id)
            pointer != null -> holdingMap.snatchAndLoad(pointer, id)
            else -> return null
        }

        return (container.getSubLevel(id) as? ServerSubLevel)
            ?: null.also { DimensionalDoors.LOGGER.warn("Sable sub-level {} did not become live after loading", id) }
    }

    private fun snatchFromOwningChunk(holdingMap: SubLevelHoldingChunkMap, id: UUID) {
        val owningChunk = SableHoldingChunks.loadedHoldingChunks(holdingMap)
            .firstOrNull { chunk -> chunk.loadedHoldingSubLevels.any { it.data().uuid() == id } }

        if (owningChunk == null) {
            DimensionalDoors.LOGGER.error("Sable sub-level {} has a holding entry but no loaded holding chunk claims it", id)
            return
        }

        holdingMap.snatchAndLoad(GlobalSavedSubLevelPointer(owningChunk.chunkPos, 0, 0), id)
    }

    private fun findStored(container: ServerSubLevelContainer, chunkPos: ChunkPos): Stored? {
        if (!isOccupiedPlot(container, chunkPos)) return null
        val (plotX, plotZ) = plotCoords(container, chunkPos)

        SableHoldingChunks.allHoldingSubLevels(container.holdingChunkMap)
            .firstOrNull { isInPlot(it.data(), plotX, plotZ) }
            ?.let { return Stored(it.data().uuid(), null) }

        val storage = container.holdingChunkMap.storage
        if (!Files.isDirectory(storage.folder)) return null

        return try {
            Files.newDirectoryStream(storage.folder, "*.slvlr").use { paths ->
                paths.firstNotNullOfOrNull { path ->
                    REGION_FILE.matchEntire(path.fileName.toString())?.destructured?.let { (regionX, regionZ) ->
                        findInRegion(storage, plotX, plotZ, regionX.toInt(), regionZ.toInt())
                    }
                }
            }
        } catch (_: IOException) {
            null
        }
    }

    private fun findInRegion(storage: SubLevelStorage, plotX: Int, plotZ: Int, regionX: Int, regionZ: Int): Stored? {
        for (localX in 0 until 32) {
            for (localZ in 0 until 32) {
                val chunkPos = ChunkPos((regionX shl 5) + localX, (regionZ shl 5) + localZ)
                val holdingChunk = storage.attemptLoadHoldingChunk(chunkPos) ?: continue

                for (pointer in holdingChunk.subLevelPointers) {
                    val data = storage.attemptLoadSubLevel(chunkPos, pointer) ?: continue
                    if (isInPlot(data, plotX, plotZ)) {
                        return Stored(data.uuid(), GlobalSavedSubLevelPointer(chunkPos, pointer.storageIndex(), pointer.subLevelIndex()))
                    }
                }
            }
        }

        return null
    }

    private fun isInPlot(data: SubLevelData, plotX: Int, plotZ: Int): Boolean {
        if (!data.fullTag().contains("plot", Tag.TAG_COMPOUND.toInt())) return false
        val plot = data.fullTag().getCompound("plot")
        return plot.getInt("plot_x") == plotX && plot.getInt("plot_z") == plotZ
    }

    private fun plotCoords(container: SubLevelContainer, chunkPos: ChunkPos): Pair<Int, Int> = Pair(
        (chunkPos.x shr container.logPlotSize) - container.origin.x,
        (chunkPos.z shr container.logPlotSize) - container.origin.y
    )

    private class Stored(val id: UUID, val pointer: GlobalSavedSubLevelPointer?)
}
