package org.dimdev.dimcore.api

import com.mojang.serialization.MapCodec
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.levelgen.carver.WorldCarver
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.storage.loot.LootTable

interface IRegister {
    fun <T: Any, V : T> register(key: ResourceKey<Registry<T>>, id: String, obj: V): V
    fun <T: Any, V : T> register(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): V
    fun <T: Any, V : T> registerHolder(key: ResourceKey<Registry<T>>, id: String, obj: V): Holder<T>
    fun <T: Any, V : T> registerHolder(key: ResourceKey<Registry<T>>, id: ResourceLocation, obj: V): Holder<T>

    fun <T : Item> registerItem(id: String, obj: T): T = register<Item, T>(Registries.ITEM, id, obj)
    fun <T : Block> registerBlock(id: String, obj: T): T = register<Block, T>(Registries.BLOCK, id, obj)
    fun <T : BlockEntityType<*>> registerBlockEntityType(id: String, obj: T): T = register<BlockEntityType<*>, T>(Registries.BLOCK_ENTITY_TYPE, id, obj)
    fun <T : EntityType<*>> registerEntityType(id: String, obj: T): T = register<EntityType<*>, T>(Registries.ENTITY_TYPE, id, obj)
    fun <T : Fluid> registerFluid(id: String, obj: T): T = register<Fluid, T>(Registries.FLUID, id, obj)
    fun <T : SoundEvent> registerSoundEvent(id: String, obj: T): T = register<SoundEvent, T>(Registries.SOUND_EVENT, id, obj)
    fun <T : MenuType<*>> registerMenu(id: String, obj: T): T = register<MenuType<*>, T>(Registries.MENU, id, obj)
    fun <T : RecipeSerializer<*>> registerRecipeSerializer(id: String, obj: T): T = register<RecipeSerializer<*>, T>(Registries.RECIPE_SERIALIZER, id, obj)
    fun <T : RecipeType<*>> registerRecipeType(id: String, obj: T): T = register<RecipeType<*>, T>(Registries.RECIPE_TYPE, id, obj)
    fun <T : ParticleType<*>> registerParticleType(id: String, obj: T): T = register<ParticleType<*>, T>(Registries.PARTICLE_TYPE, id, obj)
    fun <T : Potion> registerPotion(id: String, obj: T): T = register<Potion, T>(Registries.POTION, id, obj)
    fun registerEnchantment(id: String, obj: Enchantment): Enchantment = register<Enchantment, Enchantment>(Registries.ENCHANTMENT, id, obj)
    fun registerArmorMaterial(id: String, obj: ArmorMaterial): ArmorMaterial = register<ArmorMaterial, ArmorMaterial>(Registries.ARMOR_MATERIAL, id, obj)
    fun registerDamageType(id: String, obj: DamageType): DamageType = register<DamageType, DamageType>(Registries.DAMAGE_TYPE, id, obj)
    fun <T : DataComponentType<*>> registerDataComponentType(id: String, obj: T): T = register<DataComponentType<*>, T>(Registries.DATA_COMPONENT_TYPE, id, obj)
    fun registerCustomStat(id: String, obj: ResourceLocation): ResourceLocation = register<ResourceLocation, ResourceLocation>(Registries.CUSTOM_STAT, id, obj)
    fun <T : LootTable> registerLootTable(id: String, obj: T): T = register<LootTable, T>(Registries.LOOT_TABLE, id, obj)
    fun <T : CriterionTrigger<*>> registerTriggerType(id: String, obj: T): T = register<CriterionTrigger<*>, T>(Registries.TRIGGER_TYPE, id, obj)
    fun <T : WorldCarver<*>> registerCarver(id: String, obj: T): T = register<WorldCarver<*>, T>(Registries.CARVER, id, obj)
    fun <T : StructureProcessor> registerStructureProcessor(id: String, codec: MapCodec<T>): StructureProcessorType<T> = register<StructureProcessorType<*>, StructureProcessorType<T>>(Registries.STRUCTURE_PROCESSOR, id, StructureProcessorType<T> { codec })
    fun registerRunnable(key: ResourceKey<out Registry<*>>, runnable: () -> Unit)

    fun <T> createRegistry(key: ResourceKey<Registry<T>>, defaultId: ResourceLocation? = null , sync: Boolean = false): Registry<T>
    fun <T> createRegistry(key: ResourceKey<Registry<T>>, defaultId: ResourceLocation): Registry<T> = createRegistry(key, defaultId, false)
    fun <T> createRegistry(key: ResourceKey<Registry<T>>, sync: Boolean): Registry<T> = createRegistry(key, null, sync)

    fun <T> createRegistry(key: ResourceKey<Registry<T>>): Registry<T> = createRegistry(key, null, false)

    interface Registrar<T> {
    }
}
