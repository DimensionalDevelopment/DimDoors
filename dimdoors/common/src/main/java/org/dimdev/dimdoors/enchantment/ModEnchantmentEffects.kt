package org.dimdev.dimdoors.enchantment

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.enchantment.effect.TranscendentProjectileEffect

object ModEnchantmentEffects : PlatformRegistry<MapCodec<out EnchantmentEntityEffect>>(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, getSided()) {
    val TRANSCENDENT_PROJECTILE = create("transcendent_projectile") { TranscendentProjectileEffect.codec }
}
