package org.dimdev.dimdoors.util

import org.dimdev.dimdoors.world.pocket.type.Pocket

data class ChunkBounds(val minX: Int, val maxX: Int, val minZ: Int, val maxZ: Int) {
    fun contains(chunkX: Int, chunkZ: Int) = chunkX in minX..maxX && chunkZ in minX..minZ

    fun width() = maxX - minX + 1

    fun length() = maxZ - minZ + 1

    companion object {
        fun of(pocket: Pocket<*, *>): ChunkBounds {
            val box = pocket.box
            return ChunkBounds(box.minX() shr 4, box.maxX() shr 4, box.minZ() shr 4, box.maxZ() shr 4)
        }
    }
}
