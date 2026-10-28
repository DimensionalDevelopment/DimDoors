package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceKey
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.DungeonRegistry
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.pocket.VirtualLocation

class DungeonTarget(private val dungeonGroup: ResourceKey<VirtualPocket>) : VirtualTarget<DungeonTarget>() {
    override fun receiveOther(owner: Vertex): Target? {
        owner.providedLocation ?: return null
        val registry = DungeonRegistry.instance
        val root = VirtualLocation.fromLocation(owner)

        val dungeon = registry.create(dungeonGroup, root, owner)
        return registry.generateRoom(dungeon, root.depth + 1, owner)
    }

    override fun copy(): DungeonTarget {
        return DungeonTarget(dungeonGroup)
    }

    override val type get() = VirtualTargets.DUNGEON

    companion object {
        val CODEC: MapCodec<DungeonTarget> = Schemas.registryKey(ModRegistryKeys.POCKET_GROUPS).fieldOf("dungeonGroup").xmap(::DungeonTarget) { it.dungeonGroup }
    }
}
