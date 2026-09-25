package org.dimdev.dimcore.api.transfer

import net.minecraft.core.component.DataComponentPatch

class EnergyUnit(
    override val amount: Long
) : Unit<EnergyUnit> {
    override val components: DataComponentPatch = DataComponentPatch.EMPTY
    override val resource: Any = Energy
    override fun withAmount(amount: Long): EnergyUnit = EnergyUnit(amount)

    object Energy
}