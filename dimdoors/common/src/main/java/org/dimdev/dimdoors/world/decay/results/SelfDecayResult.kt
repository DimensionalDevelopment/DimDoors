package org.dimdev.dimdoors.world.decay.results

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.decay.Decay

object SelfDecayResult : DecayResult, SingletonInstance<SelfDecayResult>() {
    const val KEY: String = "self"

    @JvmStatic
    fun instance(): SelfDecayResult = this

    override val type get() = DecayResults.SELF

    override fun process(context: Decay.DecayContext): Int = 0

    override fun produces(): List<DecayResult.Result> = listOf()
}
