package org.dimdev.dimdoors.decay.results

import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.util.codec.SingletonInstance

object NoneDecayResult : DecayResult, SingletonInstance<NoneDecayResult>() {
    const val KEY: String = "none"

    @JvmStatic
    fun instance(): NoneDecayResult = this

    override val type get() = DecayResults.NONE

    override fun process(context: Decay.DecayContext): Int = 0

    override fun produces(): List<DecayResult.Result> = listOf()
}
