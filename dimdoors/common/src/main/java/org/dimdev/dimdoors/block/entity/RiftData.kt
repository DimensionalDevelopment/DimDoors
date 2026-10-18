package org.dimdev.dimdoors.block.entity

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.resources.RegistryFileCodec
import net.minecraft.util.Mth
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.api.util.nullableForGetter
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import kotlin.jvm.optionals.getOrNull

class RiftData {
    var destination: VirtualTarget<*> = VirtualTarget.NoneTarget
    var properties: LinkProperties? = null
    var alwaysDelete = false
    var colorOverride: RGBA? = null
    var size = 0
        set(value) {
            field = Mth.clamp(value, 0 , 150)
        }
    var color: RGBA = RGBA.NONE

    fun copy(): RiftData {
        val data = RiftData()
        data.destination = this.destination.copy()
        data.properties = this.properties?.copy()
        data.color = this.color.copy()
        data.alwaysDelete = this.alwaysDelete
        data.size = size
        data.colorOverride = this.colorOverride?.copy()
        return data
    }

    companion object {
        val CODEC = Schemas.record<RiftData>(DimensionalDoors.id("rift_data")) { instance -> instance.group(
                VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget)
                    .forGetter(RiftData::destination),
                LinkProperties.CODEC.optionalFieldOf("properties").nullableForGetter(RiftData::properties),
                RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).readOnly().forGetter(RiftData::color),
                Schemas.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::alwaysDelete),
                RGBA.CODEC.optionalFieldOf("colorOverride").nullableForGetter(RiftData::colorOverride),
                Schemas.int(0, 150).optionalFieldOf("size", 0).forGetter(RiftData::size),
                Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter<RiftData> { false }
            ).apply(instance) { destination, properties, color, alwaysDelete, colorOverride, size, forcedColor ->
                val data = RiftData()
                data.destination = destination
                data.properties = properties.getOrNull()
                data.color = color
                data.alwaysDelete = alwaysDelete
                data.colorOverride = colorOverride.getOrNull() ?: color.takeIf { forcedColor }
                data.size = size
                data
            }
        }

        val HOLDER_CODEC: Codec<Holder<RiftData>> = RegistryFileCodec.create(ModRegistryKeys.RIFT_DATA, CODEC)
    }
}
