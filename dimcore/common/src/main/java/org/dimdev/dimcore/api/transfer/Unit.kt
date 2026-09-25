package org.dimdev.dimcore.api.transfer

import net.minecraft.core.component.DataComponentPatch

interface Unit<U : Unit<U>> {
    val resource: Any
    val components: DataComponentPatch
    val amount: Long

    fun withAmount(amount: Long): U

    fun sameResource(other: U): Boolean = resource === other.resource && components == other.components

    val isEmpty: Boolean
        get() = amount <= 0

    val isNotEmpty: Boolean
        get() = !isEmpty
}
