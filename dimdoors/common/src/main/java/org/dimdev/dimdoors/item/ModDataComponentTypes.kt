package org.dimdev.dimdoors.item

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.api.util.RotatedLocation
import org.dimdev.dimdoors.entity.mask.MaskType
import org.dimdev.dimdoors.rift.targets.VirtualTarget

object ModDataComponentTypes : PlatformRegistry.DataComponentTypePlatformRegistry(getSided()) {
    val DESTINATION = create("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC.cast())
    @JvmField val COUNT = create("count", Codec.INT, ByteBufCodecs.INT.cast())
    @JvmField val MASK_TYPE = create("mask_type", MaskType.CODEC, MaskType.STREAM_CODEC)
    @JvmField val MASK_WAND_WAYPOINTS = create("mask_wand_waypoints", BlockPos.CODEC.listOf(), BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()).cast())

    @JvmField
    val VIRTUAL_TARGET = create("virtual_target", VirtualTarget.CODEC, null)
}
