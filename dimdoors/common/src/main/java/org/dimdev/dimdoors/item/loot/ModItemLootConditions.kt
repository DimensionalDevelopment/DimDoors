package org.dimdev.dimdoors.item.loot

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModItemLootConditions : PlatformRegistry<LootItemConditionType>(Registries.LOOT_CONDITION_TYPE, BuiltInRegistries.LOOT_CONDITION_TYPE, getSided()) {
    var ENTITY_NEARBY = create("entity_nearby") { LootItemConditionType(EntityNearBy.CODEC) }
}
