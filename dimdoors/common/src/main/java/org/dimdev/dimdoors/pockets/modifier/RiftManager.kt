package org.dimdev.dimdoors.pockets.modifier

import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.rift.targets.IdMarker
import kotlin.math.max

class RiftManager(val pocket: Pocket<*, *>, skipGatheringRifts: Boolean = false,
                  private var maxId: Int = 0
) {
    private val map: MutableMap<Int, BlockEntityRift<*>>

    val rifts: MutableList<BlockEntityRift<*>>

    init {
        if (skipGatheringRifts) {
            map = mutableMapOf()
            rifts = mutableListOf()
        } else {
            rifts = pocket.blockEntities.values.filterIsInstance<BlockEntityRift<*>>().toMutableList()

            map = rifts.mapNotNull { rift -> rift.data().destination.castOrNull<IdMarker>()?.id?.takeIf { it >= 0 }?.let { it to rift } }.toMap().toMutableMap()
            maxId = map.keys.maxByOrNull { it } ?: -1
        }
    }

    //TODO add javadocs
    fun add(rift: BlockEntityRift<*>): Boolean {
        rifts.add(rift)

        val id = rift.data().destination.castOrNull<IdMarker>()?.id?.takeIf { it >= 0 } ?: return false

        map[id] = rift

        maxId = max(id, maxId)

        return true
    }

    fun consume(id: Int, consumer: (BlockEntityRift<*>) -> Boolean): Boolean {
        map[id]?.takeIf(consumer) ?: return false
        map.remove(id)
        return true
    }

    fun nextId(): Int {
        return maxId + 1
    }

    fun available(id: Int): Boolean { // TODO: remove? method is likely redundant
        return !map.containsKey(id)
    }

    fun foreachConsume(consumer: (Int, BlockEntityRift<*>) -> Boolean) {
        map.entries.removeIf { consumer(it.key, it.value) }
    }
    operator fun get(id: Int): BlockEntityRift<*>? {
        return map[id]
    }
}
