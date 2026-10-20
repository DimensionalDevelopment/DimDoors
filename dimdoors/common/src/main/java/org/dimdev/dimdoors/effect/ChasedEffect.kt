package org.dimdev.dimdoors.effect

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory

private fun ServerPlayer.removeEffect(effect: MobEffect) = this.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))

class ChasedEffect : MobEffect(MobEffectCategory.HARMFUL, 0x3F3447) {
    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}
