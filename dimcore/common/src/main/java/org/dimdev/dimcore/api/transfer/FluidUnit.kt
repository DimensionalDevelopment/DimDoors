package org.dimdev.dimcore.api.transfer

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.level.material.Fluid

data class FluidUnit(val fluid: Fluid, override val components: DataComponentPatch, override val amount: Long) : Unit<FluidUnit> {
    constructor(fluid: Fluid, amount: Long) : this(fluid, DataComponentPatch.EMPTY, amount)

    override val resource: Any get() = fluid

    override fun withAmount(amount: Long): FluidUnit = copy(amount = amount)
}
