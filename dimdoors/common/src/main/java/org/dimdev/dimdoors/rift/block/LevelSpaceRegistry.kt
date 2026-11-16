package org.dimdev.dimdoors.rift.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.util.Location
import org.dimdev.dimdoors.util.ext.unboundedMap
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.util.LevelSpaceHelper
import java.util.*
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry

class LevelSpaceRegistry(private val spaces: MutableMap<UUID, UUID> = mutableMapOf()) : SubSystem<LevelSpaceRegistry>() {

    override fun type(): Type<LevelSpaceRegistry> = SubsystemTypes.LEVEL_SPACE

    fun get(rift: UUID): UUID? = spaces[rift]

    fun set(rift: UUID, space: UUID?) {
        val previous = if (space == null) spaces.remove(rift) else spaces.put(rift, space)
        if (previous != space) setDirty()
    }

    companion object {
        val CODEC: MapCodec<LevelSpaceRegistry> = UUIDUtil.STRING_CODEC.unboundedMap(UUIDUtil.CODEC).fieldOf("spaces")
            .xmap(::LevelSpaceRegistry) { it.spaces }

        val instance: LevelSpaceRegistry get() = getInstance(SubsystemTypes.LEVEL_SPACE)!!

        private fun track(id: UUID, location: Location) =
            instance.set(id, LevelSpaceHelper.INSTANCE.levelSpaceOf(location.world, location.blockPos))

        fun registerEvents() {
            LocationRiftRegistry.EVENTS.riftAddedEvent.register { id, location -> track(id, location) }
            LocationRiftRegistry.EVENTS.riftMoveEvent.register { id, _, to -> track(id, to) }
            LocationRiftRegistry.EVENTS.riftRemoveEvent.register { id, _ -> instance.set(id, null) }
        }
    }
}
