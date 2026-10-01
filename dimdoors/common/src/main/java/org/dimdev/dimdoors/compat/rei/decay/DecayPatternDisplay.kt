package org.dimdev.dimdoors.compat.rei.decay

import dev.architectury.fluid.FluidStack
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimdoors.compat.decay.DecayDisplayData
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient
import org.dimdev.dimdoors.world.decay.DecayPatternHolder
import org.dimdev.dimdoors.world.decay.results.DecayResult
import java.util.*

class DecayPatternDisplay(
    input: MutableList<EntryIngredient>,
    output: MutableList<EntryIngredient>,
    id: Optional<ResourceLocation>
) : BasicDisplay(input, output, id) {

    override fun getCategoryIdentifier() = TesselatingReiCompatClient.DECAYS_INTO

    companion object {
        fun list(patternHolder: DecayPatternHolder, registryAccess: RegistryAccess): MutableList<DecayPatternDisplay> {
            return DecayDisplayData.list(patternHolder, registryAccess)
                .map<DecayPatternDisplay>(::create)
                .filter(Objects::nonNull)
                .toList()
        }

        //    public static DecayPatternDisplay of(DecayPatternHolder patternHolder) {
        //        var pattern = patternHolder.value();
        //        var input = pattern.conditions().stream().flatMap(a -> Stream.of(a.constructApplicableBlocks(), a.constructApplicableFluids())).flatMap(Collection::stream).map(DecayPatternDisplay::toEntryStack).filter(a -> !a.isEmpty()).collect(EntryIngredient.collector());
        //        var output = pattern.result().produces().stream().map(DecayPatternDisplay::toEntryStack).collect(EntryIngredient.collector());
        //        return new DecayPatternDisplay(List.of(input), List.of(output), Optional.of(patternHolder.id()));
        //    }

        fun toEntryStack(result: DecayResult.Result): EntryStack<*> {
            return when(val obj = result.obj) {
                is Block -> EntryStack.of(VanillaEntryTypes.ITEM, ItemStack(obj, result.amount))
                is Fluid ->  EntryStack.of(VanillaEntryTypes.FLUID, FluidStack.create(obj, FluidStack.bucketAmount() * result.amount))
                else -> EntryStack.empty()
            }
        }

        fun toEntryStack(`object`: Any?): EntryStack<*> = when (`object`) {
            is ResourceKey<*> ->
                if (`object`.isFor(Registries.BLOCK)) toEntryStack(BuiltInRegistries.BLOCK.get(`object`.location()))
                else if (`object`.isFor(Registries.FLUID)) toEntryStack(BuiltInRegistries.FLUID.get(`object`.location()))
                else EntryStack.empty()
            is ItemLike -> EntryStack.of(VanillaEntryTypes.ITEM, ItemStack(`object`))
            is Fluid -> EntryStack.of(VanillaEntryTypes.FLUID, FluidStack.create(`object`, FluidStack.bucketAmount()))
            is DecayResult.Result -> toEntryStack(`object`)
            else -> EntryStack.empty()
        }

        private fun create(data: DecayDisplayData): DecayPatternDisplay? {
            val inputStack: EntryStack<*> = toEntryStack(data.input)

            if (inputStack.isEmpty) return null

            val output = data.outputs
                .map { result -> toEntryStack(result) }
                .filter { stack -> !stack.isEmpty }
                .map { EntryIngredient.of(it) }

            if (output.isEmpty()) {
                return null
            }

            return DecayPatternDisplay(
                mutableListOf(EntryIngredient.of(inputStack)),
                output.toMutableList(),
                Optional.of<ResourceLocation>(data.id)
            )
        }
    }
}
