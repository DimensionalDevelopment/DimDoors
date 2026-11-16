package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.pockets.PocketGenerationHelper
import org.dimdev.dimdoors.pockets.VirtualLocation
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.pocket.DungeonRegistry
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry

object DelvingTarget : VirtualTarget<DelvingTarget>() {
    override fun receiveOther(owner: Vertex): Target? {
        val room = PocketRegistry.instance.getPocketAt(owner) ?: return null
        val registry = DungeonRegistry.instance

        val dungeon = registry.dungeonOf(room) ?: registry.create(PocketGenerationHelper.ALL_DUNGEONS, room.virtualLocation.copy(depth = VirtualLocation.baseDepth(room.virtualLocation.world)), owner)
        return registry.generateRoom(dungeon, room.virtualLocation.depth + 1, owner)
    }

    override val type get() = VirtualTargets.DELVING

    override fun copy() = DelvingTarget

    val codec: MapCodec<DelvingTarget> = MapCodec.unit(DelvingTarget)
}
