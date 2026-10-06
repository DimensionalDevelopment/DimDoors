package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.api.util.Location
import java.util.*

abstract class RegistryVertex {
    abstract fun getLocation(id: UUID): Location?

    open fun sourceGone(self: UUID, source: Vertex, location: Location?) {}
    open fun targetGone(self: UUID, target: Vertex, location: Location?) {}

    open fun sourceAdded(self: UUID, source: Vertex) {}
    open fun targetAdded(self: UUID, target: Vertex) {}

    open fun sourceMoved(self: UUID, source: Vertex) {}
    open fun targetMoved(self: UUID, target: Vertex) {}

    open fun targetChanged(self: UUID, target: Vertex) {}

    companion object {
        val CODEC = Codec.lazyInitialized { ModRegistries.REGISTRY_VERTEX_TYPE.byNameCodec() }
    }
}
