package org.dimdev.dimdoors.decay.conditions

import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.util.codec.SingletonInstance

object NoneDecayCondition : DecayCondition, SingletonInstance<NoneDecayCondition>() {
    override val type get() = DecayConditions.NONE
    override fun test(context: Decay.DecayContext): Boolean {
        return false
    }
}