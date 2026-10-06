package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.MapCodec
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.util.LevelSpaceHelper
import java.util.*

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
            RiftRegistry.RiftEvents.RIFT_ADDED.register { id, location -> track(id, location) }
            RiftRegistry.RiftEvents.RIFT_MOVED.register { id, _, to -> track(id, to) }
            RiftRegistry.RiftEvents.RIFT_REMOVED.register { id, _ -> instance.set(id, null) }
        }
    }
}
