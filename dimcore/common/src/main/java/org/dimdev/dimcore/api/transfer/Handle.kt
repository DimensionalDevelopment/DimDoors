package org.dimdev.dimcore.api.transfer

import org.dimdev.dimcore.api.ext.cast

interface Handle<U : Unit<U>> {

    fun insert(unit: U, simulate: Boolean): Long

    fun extract(unit: U, simulate: Boolean): Long

    fun contents(): List<U>

    fun extractAny(amount: Long, simulate: Boolean): U? = contents().firstNotNullOfOrNull { held ->
        held.takeIf { it.isNotEmpty }
            ?.let { extract(it.withAmount(amount.coerceAtMost(it.amount)), simulate) }
            ?.takeIf { it > 0 }
            ?.let(held::withAmount)
    }

    private object Empty : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long = 0
        override fun extract(unit: FluidUnit, simulate: Boolean): Long = 0
        override fun contents(): List<FluidUnit> = emptyList()
    }

    companion object {
        @JvmStatic
        fun <U : Unit<U>> empty(): Handle<U> = Empty.cast()
    }
}
