package org.dimdev.dimdoors.enchantment.effect

import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Unit
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.enchantment.EnchantedItemInUse
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.DataValues

object TranscendentProjectileEffect : EnchantmentEntityEffect, SingletonInstance<TranscendentProjectileEffect>() {
    override fun apply(
        level: ServerLevel,
        enchantmentLevel: Int,
        item: EnchantedItemInUse,
        entity: Entity,
        origin: Vec3
    ) {
        if (entity is Projectile) {
            DataValues.TRANSCENDENT_PROJECTILE.set(entity, Unit.INSTANCE)
        }
    }

    override fun codec() = codec
}
