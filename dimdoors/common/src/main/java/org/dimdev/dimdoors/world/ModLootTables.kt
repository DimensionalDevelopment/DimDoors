package org.dimdev.dimdoors.world

import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.api.util.key

object ModLootTables {
    @JvmField val DUNGEON_CHEST = Registries.LOOT_TABLE.key("chest/dungeon_chest")
    @JvmField val DISPENSER_PROJECTILES = Registries.LOOT_TABLE.key("chest/dispenser_projectiles")
    @JvmField val REMOVED_RIFT = Registries.LOOT_TABLE.key("block_use/removed_rift")
    @JvmField val DISPENSER_SPLASH_POTIONS = Registries.LOOT_TABLE.key("chest/dispenser_splash_potions")
    @JvmField val DISPENSER_POTION_ARROWS = Registries.LOOT_TABLE.key("chest/dispenser_potion_arrows")
}