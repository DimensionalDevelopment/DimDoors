package org.dimdev.dimdoors.rift.targets

import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.util.CodecUtils.holderCodec
import org.dimdev.dimdoors.util.Copyable
import org.dimdev.dimdoors.util.codec
import org.slf4j.Logger
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
        return this.location == that.location
    }

    override fun hashCode(): Int = Objects.hash(this.location)

    @JvmRecord
    data class VirtualTargetType<T : VirtualTarget<*>?>(val codec: MapCodec<T?>?, val color: RGBA?) {
        companion object {
            val CODEC: Codec<VirtualTargetType<*> = ModRegistries.VIRTUAL_TYPE.byNameCodec()


            fun <T : VirtualTarget<T>> register(
                id: String?,
                codec: MapCodec<T?>?,
                color: RGBA?
            ): VirtualTargetType<T?>? {
                return getSided().register<VirtualTargetType<*>, VirtualTargetType<T?>?>(
                    ModRegistryKeys.VIRTUAL_TARGET,
                    id,
                    VirtualTargetType<T?>(codec, color)
                )
            }
        }
    }

    object NoneTarget : VirtualTarget<NoneTarget>() {
        override val type = VirtualTargets.NONE

        override var location: Location
            get() = super.location
            set(value) = logger.warn("Attempted to set location of NoneTarget to {}", value, Throwable())

        override fun equals(other: Any?) = other === NoneTarget

        override fun hashCode() = System.identityHashCode(NoneTarget)

        override fun copy() = NoneTarget

        override fun toString() = "[none]"

        private val logger: Logger = LogUtils.getLogger()

        val codec = MapCodec.unit(NoneTarget)
    }

    companion object {
        @JvmField
        val CODEC = ModRegistries.VIRTUAL_TYPE.holderCodec().dispatch(VirtualTarget<*>::type, { it.})

        val COLOR: RGBA = RGBA(1f, 0f, 0f, 1f)
    }
}