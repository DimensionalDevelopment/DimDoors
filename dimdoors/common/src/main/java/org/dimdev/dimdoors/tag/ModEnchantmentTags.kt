package org.dimdev.dimdoors.tag

import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.tag.ModWorldTags.tag

object ModEnchantmentTags {
    @JvmField val DUNGEON_LOOT = Registries.ENCHANTMENT.tag("dungeon_loot")
    @JvmField val BLOCKED_ON_FARSHOT = Registries.ENCHANTMENT.tag("blocked_on_farshot")
}
