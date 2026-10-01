package org.dimdev.dimdoors.item

import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.sound.ModSoundEvents

object ModArmorMaterials {
    val GARMENT_OF_REALITY: ArmorMaterial = register(
        "garment_of_reality",
        15,
        ModSoundEvents.ARMOR_EQUIP_THREAD,
        { { Ingredient.of(ModItems.WORLD_THREAD) } },
        intArrayOf(1, 2, 3, 1),
        0.0f,
        0.0f
    ) //TODO: DEFINE TRAITS

    val WORLD_THREAD: ArmorMaterial = register(
        "world_thread",
        15,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSoundEvents.ARMOR_EQUIP_THREAD),
        { { Ingredient.of(ModItems.WORLD_THREAD) } },
        intArrayOf(1, 2, 3, 1),
        0.0f,
        0.0f
    )

    fun register(
        name: String,
        enchantability: Int,
        equipSound: SoundEvent,
        repairIngredient: () -> () ->  Ingredient,
        protectionAmounts: IntArray,
        toughness: Float,
        knockbackResistance: Float
    ): ArmorMaterial {
        val id = DimensionalDoors.id(name)

        val map = protectionAmounts.indices.associateBy { ArmorItem.Type.entries[it] }

        return getSided().registerArmorMaterial(
            name,
            ArmorMaterial(
                map,
                enchantability,
                equipSound,
                repairIngredient(),
                listOf(ArmorMaterial.Layer(id)),
                toughness,
                knockbackResistance
            )
        )
    }

    fun init() {
    }
}
