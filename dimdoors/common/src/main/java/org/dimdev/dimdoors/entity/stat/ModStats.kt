package org.dimdev.dimdoors.entity.stat

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object ModStats : PlatformRegistry<ResourceLocation>(Registries.CUSTOM_STAT, BuiltInRegistries.CUSTOM_STAT, DimensionalDoors.getSided()) {
    @JvmField val DEATHS_IN_POCKETS: ResourceLocation = create("deaths_in_pocket")
    @JvmField val TIMES_SENT_TO_LIMBO: ResourceLocation = create("times_sent_to_limbo")
    val TIMES_TELEPORTED_BY_MONOLITH: ResourceLocation = create("times_teleported_by_monolith")
    val TIMES_BEEN_TO_DUNGEON: ResourceLocation = create("times_been_to_dungeon")

    private fun create(string: String): ResourceLocation {
        val resourceLocation = DimensionalDoors.id(string)
        create(string) { resourceLocation }
        //        CUSTOM.get(resourceLocation, statFormatter);
        return resourceLocation
    }
}
