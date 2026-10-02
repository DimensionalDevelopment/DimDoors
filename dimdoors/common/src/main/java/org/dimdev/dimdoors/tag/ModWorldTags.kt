package org.dimdev.dimdoors.tag

import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import org.dimdev.dimdoors.DimensionalDoors

object ModWorldTags {
    @JvmField val MONOLITHS_CAN_EXIST = Registries.DIMENSION_TYPE.tag("monoliths_can_exist")
    @JvmField val UNRAVELLED_FABRIC_CAN_UNRAVEL = Registries.DIMENSION_TYPE.tag("unravelled_fabric_can_unravel")


    fun <T: Any> ResourceKey<Registry<T>>.tag(id: String): TagKey<T> = TagKey.create<T>(this, DimensionalDoors.id(id))
}
