package org.dimdev.dimdoors.block.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.resources.RegistryFileCodec
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.api.util.nullableForGetter
import kotlin.jvm.optionals.getOrNull

class RiftData {
    var destination: VirtualTarget<*> = VirtualTarget.NoneTarget
    var properties: LinkProperties? = null
    var alwaysDelete = false
    var forcedColor = false
    var size = 0
    var color: RGBA = RGBA.NONE

    fun copy(): RiftData {
        val data = RiftData()
        data.destination = this.destination.copy()
        data.properties = this.properties?.copy()
        data.color = this.color.copy()
        data.alwaysDelete = this.alwaysDelete
        data.size = size
        data.forcedColor = this.forcedColor
        return data
    }

    companion object {
        val CODEC: Codec<RiftData> = RecordCodecBuilder.create { instance -> instance.group(
                VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget)
                    .forGetter(RiftData::destination),
                LinkProperties.CODEC.optionalFieldOf("properties").nullableForGetter(RiftData::properties),
                RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).forGetter(RiftData::color),
                Codec.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::alwaysDelete),
                Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter(RiftData::forcedColor),
                Codec.INT.optionalFieldOf("size", 0).forGetter(RiftData::size)
            ).apply(instance) { destination, properties, color, alwaysDelete, forcedColor, size ->
                val data = RiftData()
                data.destination = destination
                data.properties = properties.getOrNull()
                data.color = color
                data.alwaysDelete = alwaysDelete
                data.forcedColor = forcedColor
                data.size = size
                data
            }
        }

        val HOLDER_CODEC: Codec<Holder<RiftData>> = RegistryFileCodec.create(ModRegistryKeys.RIFT_DATA, CODEC)
    }
}