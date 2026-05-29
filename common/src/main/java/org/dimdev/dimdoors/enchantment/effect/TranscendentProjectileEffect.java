package org.dimdev.dimdoors.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.enchantment.TranscendentProjectiles;

public enum TranscendentProjectileEffect implements EnchantmentEntityEffect {
    INSTANCE;

    public static final MapCodec<TranscendentProjectileEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        if (entity instanceof Projectile) {
            TranscendentProjectiles.mark(entity);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
