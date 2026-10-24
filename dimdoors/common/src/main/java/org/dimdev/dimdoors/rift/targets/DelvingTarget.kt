package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.DungeonRegistry
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.rift.registry.Vertex

object DelvingTarget : VirtualTarget<DelvingTarget>() {
    override fun receiveOther(owner: Vertex): Target? {
        val room = PocketRegistry.instance.getPocketAt(owner) ?: return null
        val registry = DungeonRegistry.instance

        val dungeon = registry.dungeonOf(room) ?: registry.create(PocketGenerator.ALL_DUNGEONS, room.virtualLocation.copy(depth = 0), owner)
        return registry.generateRoom(dungeon, room.virtualLocation.depth + 1, owner)
    }

    override val type get() = VirtualTargets.DELVING

    override fun copy() = DelvingTarget

    val codec: MapCodec<DelvingTarget> = MapCodec.unit(DelvingTarget)
}
