package org.dimdev.dimdoors.decay.results

import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.util.codec.SingletonInstance

object SelfDecayResult : DecayResult, SingletonInstance<SelfDecayResult>() {
    const val KEY: String = "self"

    @JvmStatic
    fun instance(): SelfDecayResult = this

    override val type get() = DecayResults.SELF

    override fun process(level: ServerLevel, snapshot: Decay.Snapshot): Int = 0

    override fun produces(): List<DecayResult.Result> = listOf()
}
