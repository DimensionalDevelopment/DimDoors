package org.dimdev.dimdoors.util.math

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos

object GridUtil {
    /**
     * Calculates the grid position for a certain element id in the grid.
     *
     * @param id The element's id in the grid
     * @return The location on the grid
     */
    fun idToGridPos(id: Int): ChunkPos {
        if (id < 0) throw UnsupportedOperationException("Cannot get GridPos of negative id.")
        val out = if (id > 8) idToGridPos(id / 9) else ChunkPos(0, 0)
        var x = out.x * 3
        var z = out.z * 3

        val minor = (id % 9).toLong()
        x += ((minor + 1) % 3 - 1).toInt()
        z += ((minor / 3 + 1) % 3 - 1).toInt()

        return ChunkPos(x, z)
    }


    /**
     * Calculates the element id
     * 
     * @param pos The location on the grid
     * @return The id of the location on the grid
     */
    fun gridPosToID(pos: ChunkPos): Int {
        return convToID(pos.x, pos.z)
    }

    private fun convToID(chunkX: Int, chunkZ: Int): Int {
        var x = chunkX
        var z = chunkZ

        var id = Math.floorMod(x, 3) + (Math.floorMod(z, 3) * 3)

        x = Math.floorDiv(x + 1, 3)
        z = Math.floorDiv(z + 1, 3)
        if (x != 0 || z != 0) {
            id += 9 * convToID(x, z)
        }
        return id
    }

    class GridPos {
        var x: Int
        var z: Int

        constructor(x: Int, z: Int) {
            this.x = x
            this.z = z
        }

        constructor(pos: BlockPos, gridSize: Int) {
            this.x = Math.floorDiv(Math.floorDiv(pos.x, gridSize), 16)
            this.z = Math.floorDiv(Math.floorDiv(pos.z, gridSize), 16)
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is GridPos) return false
            if (this.x != other.x) return false
            return this.z == other.z
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            result = result * PRIME + this.x
            result = result * PRIME + this.z
            return result
        }

        override fun toString(): String = "GridUtils.GridPos(x=${this.x}, z=${this.z})"
    }
}
