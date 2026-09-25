package org.dimdev.dimdoors.api.util

import net.minecraft.core.Vec3i
import net.minecraft.nbt.IntArrayTag
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.structure.BoundingBox
import kotlin.math.max
import kotlin.math.min

object BlockBoxUtil {
    fun toNbt(box: BoundingBox): IntArrayTag {
        return IntArrayTag(intArrayOf(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()))
    }

    fun getBox(chunk: ChunkAccess): BoundingBox {
        val pos = chunk.pos
        return BoundingBox.fromCorners(
            Vec3i(pos.minBlockX, chunk.minBuildHeight, pos.minBlockZ),
            Vec3i(pos.maxBlockX, chunk.maxBuildHeight - 1, pos.maxBlockZ)
        )
    }

    fun intersect(box1: BoundingBox, box2: BoundingBox): BoundingBox {
        val minX = max(box1.minX(), box2.minX())
        val minY = max(box1.minY(), box2.minY())
        val minZ = max(box1.minZ(), box2.minZ())
        val maxX = min(box1.maxX(), box2.maxX())
        val maxY = min(box1.maxY(), box2.maxY())
        val maxZ = min(box1.maxZ(), box2.maxZ())

        return BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
    }
}
