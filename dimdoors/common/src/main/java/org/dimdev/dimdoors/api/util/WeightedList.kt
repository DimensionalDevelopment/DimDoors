package org.dimdev.dimdoors.api.util

import net.minecraft.util.RandomSource

open class WeightedList<T : Weighted<C>, C>(c: Collection<T> = emptyList()) : ArrayList<T>(c) {
    private val random: RandomSource = RandomSource.create()
    private var peekedRandom: T? = null
    private var peeked = false

    fun getNextRandomWeighted(context: C): T? = this.getNextRandomWeighted(context, false)

    fun peekNextRandomWeighted(context: C): T? = this.getNextRandomWeighted(context, true)

    private fun getNextRandomWeighted(context: C, peek: Boolean): T? {
        if (!this.peeked) {
            var cursor = this.random.nextDouble() * getTotalWeight(context)
            if (cursor == 0.0) {
                for (weighted in this) {
                    if (weighted.getWeight(context) != 0.0) return weighted
                }
            }
            for (weighted in this) {
                cursor -= weighted.getWeight(context)
                if (cursor <= 0) {
                    if (peek) {
                        this.peekedRandom = weighted
                        this.peeked = true
                    }
                    return weighted // should never return an entry with weight 0, unless there are only weight 0 entries
                }
            }
            if (peek) {
                this.peekedRandom = null
                this.peeked = true
            }
            return null
        }
        if (!peek) this.peeked = false
        return this.peekedRandom
    }

    fun getTotalWeight(context: C): Double = this.sumOf { it.getWeight(context) }
}
