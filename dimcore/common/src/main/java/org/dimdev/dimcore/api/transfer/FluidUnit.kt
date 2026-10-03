package org.dimdev.dimcore.api.transfer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids

data class FluidUnit(val fluid: Fluid, override val components: DataComponentPatch, override val amount: Long) : Unit<FluidUnit> {
    constructor(fluid: Fluid, amount: Long) : this(fluid, DataComponentPatch.EMPTY, amount)

    override val resource: Any get() = fluid

    override fun withAmount(amount: Long): FluidUnit = copy(amount = amount)

    companion object {
        @JvmField
        val CODEC: Codec<FluidUnit> = RecordCodecBuilder.create { instance ->
            instance.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidUnit::fluid),
                DataComponentPatch.CODEC.fieldOf("components").forGetter(FluidUnit::components),
                Codec.LONG.fieldOf("amount").forGetter(FluidUnit::amount)
            ).apply(instance, ::FluidUnit)
        }

        private val EMPTY = FluidUnit(Fluids.EMPTY, 0)

        fun empty(): FluidUnit = EMPTY
    }
}
