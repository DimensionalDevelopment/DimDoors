package org.dimdev.dimdoors.util

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource

object Utils {
    fun randomInSphere(random: RandomSource, count: Int, pos: BlockPos, radius: Int): Iterable<BlockPos> {
        require(radius >= 0) { "Radius must be non-negative" }

        return Iterable {
            object : Iterator<BlockPos> {
                private val diameter = radius * 2 + 1
                private val radiusSquared = radius.toLong() * radius
                private var remaining = count

                override fun hasNext(): Boolean {
                    return this.remaining > 0
                }

                override fun next(): BlockPos {
                    if (!this.hasNext()) {
                        throw NoSuchElementException()
                    }

                    this.remaining--

                    var xOffset: Int
                    var yOffset: Int
                    var zOffset: Int
                    do {
                        xOffset = random.nextInt(this.diameter) - radius
                        yOffset = random.nextInt(this.diameter) - radius
                        zOffset = random.nextInt(this.diameter) - radius
                    } while (xOffset.toLong() * xOffset + yOffset.toLong() * yOffset + zOffset.toLong() * zOffset > this.radiusSquared)

                    return pos.offset(xOffset, yOffset, zOffset)
                }
            }
        }
    }

    fun <K, V> mergeMaps(target: MutableMap<K?, V?>, source: MutableMap<K?, V?>) {
        source.forEach { (key: K?, value: V?) -> target.putIfAbsent(key, value) }
    }
}
