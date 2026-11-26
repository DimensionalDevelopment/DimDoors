package org.dimdev.dimdoors.rift

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.resources.RegistryFileCodec
import net.minecraft.util.Mth
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.RGBA
import org.dimdev.dimdoors.util.ext.nullableForGetter
import kotlin.jvm.optionals.getOrNull

class RiftData {
    var destination: VirtualTarget<*> = VirtualTarget.NoneTarget
    var alwaysDelete = false
    var colorOverride: RGBA? = null
    var color: RGBA = RGBA.NONE

    var size: Int = 0
        set(value) {
            field = Mth.clamp(value, 0 , 150)
        }

    var weight: Int = 5
        set(value) {
            field = Mth.clamp(value, -100, 100)
        }

    fun copy(): RiftData {
        val data = RiftData()
        data.destination = this.destination.copy()
        data.color = this.color.copy()
        data.alwaysDelete = this.alwaysDelete
        data.size = size
        data.weight = weight
        data.colorOverride = this.colorOverride?.copy()
        return data
    }

    companion object {
        val CODEC = Schemas.record<RiftData>(DimensionalDoors.id("rift_data")) { instance -> instance.group(
                VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget).forGetter(RiftData::destination),
                RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).readOnly().forGetter(RiftData::color),
                Schemas.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::alwaysDelete),
                RGBA.CODEC.optionalFieldOf("colorOverride").nullableForGetter(RiftData::colorOverride),
                Schemas.int(0, 150).optionalFieldOf("size", 0).forGetter(RiftData::size),
                Schemas.int(-100, 100, 5).optionalFieldOf("weight", 5).forGetter(RiftData::weight),
                Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter { false }
            ).apply(instance) { destination, color, alwaysDelete, colorOverride, size, weight, forcedColor ->
                val data = RiftData()
                data.destination = destination
                data.color = color
                data.alwaysDelete = alwaysDelete
                data.colorOverride = colorOverride.getOrNull() ?: color.takeIf { forcedColor }
                data.size = size
                data.weight = weight
                data
            }
        }

        val HOLDER_CODEC: Codec<Holder<RiftData>> = RegistryFileCodec.create(ModRegistryKeys.RIFT_DATA, CODEC)
    }
}
