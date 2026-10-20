package org.dimdev.dimdoors.effect

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object ModMobEffects : PlatformRegistry<MobEffect>(Registries.MOB_EFFECT, BuiltInRegistries.MOB_EFFECT, DimensionalDoors.getSided()
) {
    val CHASED: ChasedEffect = create("chased") { ChasedEffect() }
}

fun MobEffect.createInstance(duration: Int, amplifier: Int, ambient: Boolean, visible: Boolean, showIcon: Boolean): MobEffectInstance {
    return MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this), duration, amplifier, ambient, visible, showIcon)
}
