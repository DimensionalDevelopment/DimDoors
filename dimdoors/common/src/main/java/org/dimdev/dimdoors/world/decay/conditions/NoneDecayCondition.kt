package org.dimdev.dimdoors.world.decay.conditions

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.decay.Decay

object NoneDecayCondition : DecayCondition, SingletonInstance<NoneDecayCondition>() {
    override val type get() = DecayConditions.NONE
    override fun test(context: Decay.DecayContext): Boolean {
        return false
    }
}