package org.dimdev.dimdoors.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.enchantment.effect.TranscendentProjectileEffect;

public final class ModEnchantmentEffects {
    private ModEnchantmentEffects() {
    }

    public static void init() {
        register("transcendent_projectile", TranscendentProjectileEffect.CODEC);
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return DimensionalDoors.getSided().register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, id, codec);
    }
}
