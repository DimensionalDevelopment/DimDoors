package org.dimdev.dimcore.api.transfer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentPatch

class EnergyUnit(
    override val amount: Long
) : Unit<EnergyUnit> {
    override val components: DataComponentPatch = DataComponentPatch.EMPTY
    override val resource: Any = Energy
    override fun withAmount(amount: Long): EnergyUnit = EnergyUnit(amount)

    object Energy

    companion object {
        @JvmField
        val CODEC: Codec<EnergyUnit> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.LONG.fieldOf("amount").forGetter(EnergyUnit::amount)
            ).apply(instance, ::EnergyUnit)
        }

        private val EMPTY = EnergyUnit(0)

        fun empty(): EnergyUnit = EMPTY
    }
}