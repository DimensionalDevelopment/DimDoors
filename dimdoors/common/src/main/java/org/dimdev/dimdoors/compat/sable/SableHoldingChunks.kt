package org.dimdev.dimdoors.compat.sable

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkMapAccessor

internal object SableHoldingChunks {
    fun loadedHoldingChunks(map: SubLevelHoldingChunkMap): MutableCollection<SubLevelHoldingChunk> {
        return (map as SubLevelHoldingChunkMapAccessor).`dimdoors$getLoadedHoldingChunks`().values
    }

    fun allHoldingSubLevels(map: SubLevelHoldingChunkMap): MutableCollection<HoldingSubLevel> {
        return (map as SubLevelHoldingChunkMapAccessor).`dimdoors$getAllHoldingSubLevels`().values
    }
}
