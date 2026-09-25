package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import java.util.*
import java.util.function.Function

class PlayerRiftPointer() : RegistryVertex() {
    constructor(id: UUID?) : this() {
        this.id = id
    }

    override val type = RegistryVertices.PLAYER

    override fun toString(): String = "PlayerRiftPointer(id=${this.id})"

    companion object {
        val MAP_CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::id)
            ).apply(instance, ::PlayerRiftPointer)
        }
    }
}
