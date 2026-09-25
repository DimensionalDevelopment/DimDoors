package org.dimdev.dimdoors.item

import com.mojang.serialization.Codec
import net.minecraft.network.codec.ByteBufCodecs
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.api.util.RotatedLocation
import org.dimdev.dimdoors.rift.targets.VirtualTarget

object ModDataComponentTypes : PlatformRegistry.DataComponentTypePlatformRegistry(getSided()) {
    val DESTINATION = create("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC)
    @JvmField val COUNT = create("count", Codec.INT, ByteBufCodecs.INT.cast())

    @JvmField
    val VIRTUAL_TARGET = create("virtual_target", VirtualTarget.CODEC, null)
}
