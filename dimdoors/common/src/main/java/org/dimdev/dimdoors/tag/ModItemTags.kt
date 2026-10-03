package org.dimdev.dimdoors.tag

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.DyeColor
import org.dimdev.dimdoors.api.util.tag

object ModItemTags {
    val LIMBO_GAZE_DEFYING = Registries.ITEM.tag("limbo_gaze_defying")
    val DRIFTWOOD_LOGS = Registries.ITEM.tag("driftwood_logs")
    val DIMENSIONAL_DOORS = Registries.ITEM.tag("dimensional_doors")
    val FABRIC = Registries.ITEM.tag("fabric")
    val ANCIENT_FABRIC = Registries.ITEM.tag("ancient_fabric")
    val TRANSCENDENT_ENCHANTABLE = Registries.ITEM.tag("enchantable/transcendent")

    val DYES = DyeColor.entries.map(::dye)

    private fun dye(dyeColor: DyeColor) = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes/${dyeColor.serializedName}"))
}
