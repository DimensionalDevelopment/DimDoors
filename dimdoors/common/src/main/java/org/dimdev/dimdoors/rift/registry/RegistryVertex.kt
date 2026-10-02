package org.dimdev.dimdoors.rift.registry

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.MapCodecHasHolder
import java.util.*

abstract class RegistryVertex : MapCodecHasHolder<RegistryVertex> {
    var world: ResourceKey<Level>? = null

    var id: UUID = UUID.randomUUID()

    open fun sourceGone(source: RegistryVertex) {}
    open fun targetGone(target: RegistryVertex) {}

    open fun sourceAdded(source: RegistryVertex) {}
    open fun targetAdded(target: RegistryVertex) {}

    open fun sourceMoved(source: RegistryVertex) {}
    open fun targetMoved(target: RegistryVertex) {}

    override fun toString(): String = "RegistryVertex(dim=${this.world}, id=${this.id})"

    companion object {
        val CODEC = RegistryVertices.codec
    }
}
