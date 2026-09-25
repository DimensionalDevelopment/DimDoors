package org.dimdev.dimdoors.world.decay.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.decay.Decay

object NoneDecayCondition : DecayCondition, SingletonInstance<NoneDecayCondition>() {
    override val type: Holder<out MapCodec<out DecayCondition>> get() = DecayConditions.NONE
    override fun test(context: Decay.DecayContext): Boolean {
        return false
    }
}