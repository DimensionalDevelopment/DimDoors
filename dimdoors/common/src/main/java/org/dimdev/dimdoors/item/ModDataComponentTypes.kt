package org.dimdev.dimdoors.item

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.entity.mask.MaskType
import org.dimdev.dimdoors.rift.RiftData
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.RotatedLocation

object ModDataComponentTypes : PlatformRegistry.DataComponentTypePlatformRegistry(getSided()) {
    val DESTINATION = create("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC.cast())
    @JvmField val MASK_TYPE = create("mask_type", MaskType.CODEC, MaskType.STREAM_CODEC)
    @JvmField val MASK_WAND_WAYPOINTS = create("mask_wand_waypoints", BlockPos.CODEC.listOf(), BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()).cast())

    @JvmField
    val VIRTUAL_TARGET = create("virtual_target", VirtualTarget.CODEC)

    val RIFT_DATA = create("rift_data", RiftData.CODEC)
    val RIFT_CURVE = create("rift_curve", Codec.INT, ByteBufCodecs.INT.cast())
    val RIFT_YAW = create("rift_yaw", Codec.FLOAT, ByteBufCodecs.FLOAT.cast())

    val TRACKED_ITEM = create("tracked_item", UUIDUtil.CODEC, UUIDUtil.STREAM_CODEC.cast())
}
