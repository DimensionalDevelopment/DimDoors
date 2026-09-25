package org.dimdev.dimdoors.pockets.modifier

import org.dimdev.dimcore.api.cast
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.targets.IdMarker
import org.dimdev.dimdoors.world.pocket.type.Pocket
import kotlin.math.max

class RiftManager(val pocket: Pocket<*, *>, skipGatheringRifts: Boolean = false,
                                            private var maxId: Int = 0
) {
    private val map: MutableMap<Int, Rift>

    val rifts: MutableList<Rift>

    init {
        if (skipGatheringRifts) {
            map = mutableMapOf()
            rifts = mutableListOf()
        } else {
            rifts = pocket.blockEntities.values.filterIsInstance<Rift>().toMutableList()

            map = rifts
                .filter { a -> (a.data.destination.castOrNull<IdMarker>()?.id ?: 0) >= 0 }.associateBy { it.data.destination.cast<IdMarker>().id }.toMutableMap()
            maxId = map.keys.maxByOrNull { it } ?: -1
        }
    }

    //TODO add javadocs
    fun add(rift: Rift): Boolean {
        rifts.add(rift)

        val id = rift.data.castOrNull<IdMarker>()?.id?.takeIf { it < 0 } ?: return false

        map[id] = rift

        maxId = max(id, maxId)

        return true
    }

    fun consume(id: Int, consumer: (Rift) -> Boolean): Boolean {
        map[id]?.takeIf(consumer) ?: return false
        map.remove(id)
        return true;
    }

    fun nextId(): Int {
        return maxId + 1
    }

    fun available(id: Int): Boolean { // TODO: remove? method is likely redundant
        return !map.containsKey(id)
    }

    fun foreachConsume(consumer: (Int, Rift) -> Boolean) {
        map.entries.removeIf { consumer(it.key, it.value) }
    }
    operator fun get(id: Int): Rift? {
        return map[id]
    }
}
