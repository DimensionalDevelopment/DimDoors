package org.dimdev.dimdoors.decay.conditions

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource
import org.dimdev.dimdoors.util.codec.SingletonInstance

object NoneDecayCondition : DecayCondition, SingletonInstance<NoneDecayCondition>() {
    override val type get() = DecayConditions.NONE
    override fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean = false
}