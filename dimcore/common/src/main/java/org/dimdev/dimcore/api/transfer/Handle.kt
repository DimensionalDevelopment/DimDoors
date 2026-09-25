package org.dimdev.dimcore.api.transfer

import org.dimdev.dimcore.api.cast

interface Handle<U : Unit<U>> {

    fun insert(unit: U, simulate: Boolean): Long

    fun extract(unit: U, simulate: Boolean): Long

    fun contents(): List<U>

    fun extractAny(amount: Long, simulate: Boolean): U? {
        return contents().filter { it.isNotEmpty }
            .map(
                { extract(it.withAmount(amount.coerceAtMost(it.amount)), simulate) },
                { it > 0},
                {unit, amount -> unit.withAmount(amount)})
            .firstOrNull()
    }

    private object Empty : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long = 0
        override fun extract(unit: FluidUnit, simulate: Boolean): Long = 0
        override fun contents(): List<FluidUnit> = emptyList()
    }

    companion object {
        fun <U : Unit<U>> empty(): Handle<U> = Empty.cast()
    }
}

inline fun <T, R> Iterable<T>.map(transform: (T) -> R, filter: (R) -> Boolean, map: (T, R) -> T):List<T> {
    return this.map {
        val r = transform.invoke(it)
        if(filter.invoke(r)) map.invoke(it, r) else it
    }
}
