package org.dimdev.dimdoors.tag

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.enchantment.Enchantment
import org.dimdev.dimdoors.DimensionalDoors

object ModEnchantmentTags {
    @JvmField
    val DUNGEON_LOOT: TagKey<Enchantment?> = of("dungeon_loot")
    @JvmField
    val BLOCKED_ON_FARSHOT: TagKey<Enchantment?> = of("blocked_on_farshot")

    private fun of(id: String?): TagKey<Enchantment?> {
        return TagKey.create<T?>(Registries.ENCHANTMENT, DimensionalDoors.id(id!!))
    }
}
