package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.util.Copyable

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
abstract class VirtualTarget<T : VirtualTarget<T>> : Target, Copyable<T>, MapCodecHasHolder<VirtualTarget<*>> {
    open fun register(owner: Vertex) {}

    open fun unregister(owner: Vertex) {}

    open fun shouldInvalidate(riftDeleted: Location) = false

    open fun getColor(owner: Vertex) = COLOR

    object NoneTarget : VirtualTarget<NoneTarget>() {
        override val type get() = VirtualTargets.NONE

        override fun copy() = NoneTarget

        override fun toString() = "[none]"

        val codec = MapCodec.unit(NoneTarget)
    }

    companion object {
        @JvmField
        val CODEC = VirtualTargets.codec

        val COLOR: RGBA = RGBA(1f, 0f, 0f, 1f)
    }
}