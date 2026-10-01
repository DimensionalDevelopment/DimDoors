package org.dimdev.dimdoors.world.decay.results

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.decay.Decay

object NoneDecayResult : DecayResult, SingletonInstance<NoneDecayResult>() {
    const val KEY: String = "none"

    @JvmStatic
    fun instance(): NoneDecayResult = this

    override val type get() = DecayResults.NONE

    override fun process(context: Decay.DecayContext): Int = 0

    override fun produces(): List<DecayResult.Result> = listOf()
}
