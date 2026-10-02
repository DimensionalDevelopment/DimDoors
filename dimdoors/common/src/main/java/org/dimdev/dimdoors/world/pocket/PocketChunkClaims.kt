package org.dimdev.dimdoors.world.pocket

import net.minecraft.world.level.chunk.ChunkAccess
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.util.ChunkBounds
import org.dimdev.dimdoors.world.DataValues
import org.dimdev.dimdoors.world.pocket.type.Pocket

object PocketChunkClaims {
    fun hasClaimedChunk(pocket: Pocket<*, *>): Boolean {
        val level = DimensionalDoors.getWorld(pocket.world) ?: return false

        val bounds = ChunkBounds.of(pocket)
        for (cx in bounds.minX..bounds.maxX) {
            for (cz in bounds.minZ..bounds.maxZ) {
                if (isClaimed(level.getChunk(cx, cz))) {
                    return true
                }
            }
        }

        return false
    }

    fun claimChunks(pocket: Pocket<*, *>) {
        val level = DimensionalDoors.getWorld(pocket.world) ?: return

        val bounds = ChunkBounds.of(pocket)

        for (cx in bounds.minX..bounds.maxX) {
            for (cz in bounds.minZ..bounds.maxZ) {
                val chunk: ChunkAccess = level.getChunk(cx, cz)
                if (!isClaimed(chunk)) {
                    DataValues.POCKET_GENERATED.set(chunk, true)
                    chunk.isUnsaved = true
                }
            }
        }
    }

    fun isClaimed(chunk: ChunkAccess?): Boolean {
        return DataValues.POCKET_GENERATED.getOrDefault(chunk, false)
    }
}
