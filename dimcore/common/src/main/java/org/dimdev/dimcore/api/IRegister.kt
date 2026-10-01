package org.dimdev.dimcore.api

import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.level.storage.loot.LootTable

interface IRegister {
    fun <T: Any, V : T> register(key: ResourceKey<Registry<T>>, id: String, obj: V): V
    fun <T: Any, V : T> register(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): V
    fun <T: Any, V : T> registerHolder(key: ResourceKey<Registry<T>>, id: String, obj: V): Holder<T>
    fun <T: Any, V : T> registerHolder(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): Holder<T>

    fun registerArmorMaterial(id: String, obj: ArmorMaterial): ArmorMaterial = register<ArmorMaterial, ArmorMaterial>(Registries.ARMOR_MATERIAL, id, obj)
    fun <T : LootTable> registerLootTable(id: String, obj: T): T = register<LootTable, T>(Registries.LOOT_TABLE, id, obj)
    fun <T : CriterionTrigger<*>> registerTriggerType(id: String, obj: T): T = register<CriterionTrigger<*>, T>(Registries.TRIGGER_TYPE, id, obj)

    fun <T> createRegistry(key: ResourceKey<Registry<T>>, sync: Boolean): Registry<T> = createRegistry(key, null, sync)

    fun <T> createRegistry(key: ResourceKey<Registry<T>>): Registry<T> = createRegistry(key, null, false)

}
