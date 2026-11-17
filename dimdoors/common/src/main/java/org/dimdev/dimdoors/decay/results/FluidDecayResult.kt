package org.dimdev.dimdoors.decay.results

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecayInventoryHelper

class FluidDecayResult(private val entropy: Int, private val worldThreadChance: Float, private val fluid: Fluid) : DecayResult {
    override val type get() = DecayResults.FLUID

    override fun entropy(): Int = entropy

    override fun worldThreadChance(): Float = worldThreadChance

    override fun process(level: ServerLevel, snapshot: Decay.Snapshot): Int {
        val pos = snapshot.pos
        val contents = DecayInventoryHelper.takeContents(level, pos)
        val newState = fluid.defaultFluidState().createLegacyBlock()
        level.setBlockAndUpdate(pos, newState)
        DecayInventoryHelper.transferOrDrop(level, pos, contents)
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
