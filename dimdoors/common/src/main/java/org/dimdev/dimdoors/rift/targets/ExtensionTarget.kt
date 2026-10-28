package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.DungeonRegistry
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.rift.registry.Vertex

object ExtensionTarget : VirtualTarget<ExtensionTarget>() {
    override fun receiveOther(owner: Vertex): Target? {
        val room = PocketRegistry.instance.getPocketAt(owner) ?: return null
        val registry = DungeonRegistry.instance
        val dungeon = registry.dungeonOf(room) ?: registry.create(PocketGenerator.ALL_DUNGEONS, room.virtualLocation.copy(depth = 0), owner)

        val depth = room.virtualLocation.depth - 1

        if(depth < 0) return registry.linkToRoot(dungeon, owner)

        return registry.generateRoom(dungeon, depth, owner)
    }

    override val type get() = VirtualTargets.EXTENSION

    override fun copy() = ExtensionTarget

    val codec: MapCodec<ExtensionTarget> = MapCodec.unit(ExtensionTarget)
}
