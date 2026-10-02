package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.util.Copyable
import java.util.*

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
abstract class VirtualTarget<T : VirtualTarget<T>> : Target, Copyable<T>, MapCodecHasHolder<VirtualTarget<*>> {
    open lateinit var location: Location

    open fun register() {}

    open fun unregister() {}

    open fun shouldInvalidate(riftDeleted: Location) = false

    open val color: RGBA get() = COLOR

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        val that = other as VirtualTarget<*>
        return this.locationOrNull == that.locationOrNull
    }

    override fun hashCode(): Int = Objects.hash(this.locationOrNull)

    val locationOrNull: Location? get() = if (this::location.isInitialized) location else null

    object NoneTarget : VirtualTarget<NoneTarget>() {
        override val type get() = VirtualTargets.NONE

        override var location: Location
            get() = super.location
            set(_) {}

        override fun equals(other: Any?) = other === NoneTarget

        override fun hashCode() = System.identityHashCode(NoneTarget)

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