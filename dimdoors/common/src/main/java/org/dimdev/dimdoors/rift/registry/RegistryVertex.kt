package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.util.CodecUtils.holderCodec
import org.dimdev.dimdoors.util.codec
import java.util.*
import java.util.function.Function

abstract class RegistryVertex : MapCodecHasHolder<RegistryVertex> {
    var world: ResourceKey<Level>? = null

    var id: UUID = UUID.randomUUID()

    open fun sourceGone(source: RegistryVertex) {}
    open fun targetGone(target: RegistryVertex) {}

    open fun sourceAdded(source: RegistryVertex) {}
    open fun targetAdded(target: RegistryVertex) {}

    open fun sourceMoved(source: RegistryVertex) {}
    open fun targetMoved(target: RegistryVertex) {}

    abstract override val type: Holder<out MapCodec<out RegistryVertex>>

    override fun toString(): String = "RegistryVertex(dim=${this.world}, id=${this.id})"

    companion object {
        val CODEC = RegistryVertices.codec
    }
}
