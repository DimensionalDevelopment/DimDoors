package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.*

class PocketEntrancePointer : RegistryVertex {
    var pocketId: Int = 0
        private set

    constructor(pocketDim: ResourceKey<Level>, pocketId: Int) {
        this.world = pocketDim
        this.pocketId = pocketId
    }

    private constructor(id: UUID, pocketDim: ResourceKey<Level>, pocketId: Int) : this(pocketDim, pocketId) {
        this.id = id
    }

    override val type = RegistryVertices.ENTRANCE

    override fun toString(): String {
        return "PocketEntrancePointer(pocketDim=" + this.world + ", pocketId=" + this.pocketId + ")"
    }

    companion object {
        // TODO: PocketRiftPointer superclass?
        val MAP_CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(PocketEntrancePointer::id),
                Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PocketEntrancePointer::world),
                Codec.INT.fieldOf("pocket_id").forGetter(PocketEntrancePointer::pocketId)
            ).apply(instance, ::PocketEntrancePointer)
        }

        val CODEC = MAP_CODEC.codec()

    }
}
