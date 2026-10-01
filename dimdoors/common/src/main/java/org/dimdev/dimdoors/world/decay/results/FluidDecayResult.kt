package org.dimdev.dimdoors.world.decay.results

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper

class FluidDecayResult(private val entropy: Int, private val worldThreadChance: Float, private val fluid: Fluid) : DecayResult {
    override val type get() = DecayResults.FLUID

    override fun entropy(): Int = entropy

    override fun worldThreadChance(): Float = worldThreadChance

    override fun process(context: Decay.DecayContext): Int {
        val contents = DecayInventoryHelper.takeContents(context.world, context.targetBlockPos)
        val newState = fluid.defaultFluidState().createLegacyBlock()
        context.world.setBlockAndUpdate(context.targetBlockPos, newState)
        DecayInventoryHelper.transferOrDrop(context.world, context.targetBlockPos, contents)
        return entropy
    }

    override fun produces(): List<DecayResult.Result> = listOf(DecayResult.Result(fluid, 1))

    companion object {
        @JvmField
        val CODEC: MapCodec<FluidDecayResult> = RecordCodecBuilder.mapCodec { instance ->
            DecayResult.entropyCodec(instance).and(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter { blockDecayResult -> blockDecayResult.fluid }).apply(instance, ::FluidDecayResult)
        }

        const val KEY: String = "fluid"

        private fun <T : Comparable<T>> transferProperty(from: FluidState, to: FluidState, property: Property<T>): FluidState {
            return to.setValue(property, from.getValue(property))
        }
    }
}
