package org.dimdev.dimdoors.compat.rei.decay

import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator
import me.shedaniel.rei.api.client.view.ViewSearchBuilder
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.world.decay.Decay.DecayLoader.getPatterns
import java.util.*

class DecayDisplayGenerator : DynamicDisplayGenerator<DecayPatternDisplay> {
    override fun getRecipeFor(entry: EntryStack<*>): Optional<MutableList<DecayPatternDisplay>> {
        return Optional.of<MutableList<DecayPatternDisplay>>(filter(entry, false))
    }

    override fun getUsageFor(entry: EntryStack<*>): Optional<MutableList<DecayPatternDisplay>> {
        return Optional.of(filter(entry, true))
    }

    override fun generate(builder: ViewSearchBuilder): Optional<MutableList<DecayPatternDisplay>> {
        val displays = displays

        if (!builder.usagesFor.isEmpty()) {
            displays.removeIf { display ->
                builder.usagesFor.none {  entry -> matches(display.inputEntries, entry) }
            }
        }

        if (!builder.recipesFor.isEmpty()) {
            displays.removeIf { display -> builder.recipesFor.stream().noneMatch { entry -> matches(display.outputEntries, entry) } }
        }

        return Optional.of((displays))
    }

    companion object {
        private fun filter(entry: EntryStack<*>, usage: Boolean): MutableList<DecayPatternDisplay> {
            return displays
                .filter { display ->
                    matches(
                        if (usage) display.inputEntries else display.outputEntries,
                        entry
                    )
                }.toMutableList()
        }

        private fun matches(ingredients: MutableList<EntryIngredient>, entry: EntryStack<*>): Boolean {
            return ingredients
                .flatten()
                .any { stack -> EntryStacks.equalsFuzzy(stack, entry) }
        }

        private val displays: MutableList<DecayPatternDisplay>
            get() {
                val server = server

                return getPatterns().values
                    .flatten()
                    .distinct()
                    .flatMap {  pattern -> DecayPatternDisplay.list(pattern, server.registryAccess()) }
                    .toMutableList()
            }
    }
}
