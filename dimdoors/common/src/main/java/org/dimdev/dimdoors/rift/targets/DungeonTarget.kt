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
        val location = owner.providedLocation ?: return null
        val registry = DungeonRegistry.instance

        val dungeon = registry.create(dungeonGroup, VirtualLocation(location.worldId, location.x, location.z, 0), owner)
        return registry.generateRoom(dungeon, 1, owner)
    }

    override fun copy(): DungeonTarget {
        return DungeonTarget(dungeonGroup)
    }

    override val type get() = VirtualTargets.DUNGEON

    companion object {
        val CODEC: MapCodec<DungeonTarget> = Schemas.registryKey(ModRegistryKeys.POCKET_GROUPS).fieldOf("dungeonGroup").xmap(::DungeonTarget) { it.dungeonGroup }
    }
}
