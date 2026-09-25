package org.dimdev.dimdoors.api.util.math

import net.minecraft.core.BlockPos

object GridUtil {
    /**
     * Calculates the grid position for a certain element id in the grid.
     *
     * @param id The element's id in the grid
     * @return The location on the grid
     */
    fun idToGridPos(id: Int): GridPos {
        if (id < 0) throw UnsupportedOperationException("Cannot get GridPos of negative id.")
        val out = if (id > 8) idToGridPos(id / 9) else GridPos(0, 0)
        var x = out.x * 3
        var z = out.z * 3

        val minor = (id % 9).toLong()
        x += ((minor + 1) % 3 - 1).toInt()
        z += ((minor / 3 + 1) % 3 - 1).toInt()

        out.x = x
        out.z = z
        return out
    }


    /**
     * Calculates the element id
     * 
     * @param pos The location on the grid
     * @return The id of the location on the grid
     */
    fun gridPosToID(pos: GridPos): Int {
        return convToID(pos)
    }

    private fun convToID(pos: GridPos): Int {
        var x = pos.x
        var z = pos.z

        var id = Math.floorMod(x, 3) + (Math.floorMod(z, 3) * 3)

        x = Math.floorDiv(x + 1, 3)
        z = Math.floorDiv(z + 1, 3)
        if (x != 0 || z != 0) {
            pos.x = x
            pos.z = z
            id += 9 * convToID(pos)
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
