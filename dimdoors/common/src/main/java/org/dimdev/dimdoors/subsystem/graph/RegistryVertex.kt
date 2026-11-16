package org.dimdev.dimdoors.subsystem.graph

import com.mojang.serialization.Codec
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.rift.target.TargetResolver
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.util.Location
import java.util.*

abstract class RegistryVertex {
    abstract fun getLocation(id: UUID): Location?

    open fun getTarget(id: UUID): Target? = TargetResolver.target(getLocation(id))

    open fun sourceGone(self: UUID, source: Vertex) {}
    open fun targetGone(self: UUID, target: Vertex) {}

    open fun sourceAdded(self: UUID, source: Vertex) {}
    open fun targetAdded(self: UUID, target: Vertex) {}

    open fun sourceMoved(self: UUID, source: Vertex) {}
    open fun targetMoved(self: UUID, target: Vertex) {}

    open fun targetChanged(self: UUID, target: Vertex) {}

    open fun pocketChained(self: UUID, pocket: Pocket<*, *>) {}

    companion object {
        val CODEC: Codec<RegistryVertex> = Codec.lazyInitialized { ModRegistries.REGISTRY_VERTEX_TYPE.byNameCodec() }
    }
}
