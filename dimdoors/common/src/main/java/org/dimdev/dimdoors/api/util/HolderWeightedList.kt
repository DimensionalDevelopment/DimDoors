package org.dimdev.dimdoors.api.util

import net.minecraft.core.Holder
import net.minecraft.util.RandomSource

open class HolderWeightedList<T : Weighted<C>, C>(c: Collection<Holder<T>> = emptyList()) : ArrayList<Holder<T>>(c) {
    private val random: RandomSource = RandomSource.create()
    private var peekedRandom: Holder<T>? = null
    private var peeked = false

    fun getNextRandomWeighted(context: C): Holder<T>? = this.getNextRandomWeighted(context, false)

    fun peekNextRandomWeighted(context: C): Holder<T>? = this.getNextRandomWeighted(context, true)

    private fun getNextRandomWeighted(context: C, peek: Boolean): Holder<T>? {
        if (!this.peeked) {
            var cursor = this.random.nextDouble() * getTotalWeight(context)
            if (cursor == 0.0) {
                for (weighted in this) {
                    if (weighted.value().getWeight(context) != 0.0) return weighted
                }
            }
            for (weighted in this) {
                cursor -= weighted.value().getWeight(context)
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

    fun getTotalWeight(context: C): Double = this.sumOf { it.value().getWeight(context) }
}
