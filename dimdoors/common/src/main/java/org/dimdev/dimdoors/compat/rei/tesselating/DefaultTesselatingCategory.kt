package org.dimdev.dimdoors.compat.rei.tesselating

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Slot
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.display.DisplayMerger
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient

class DefaultTesselatingCategory : DisplayCategory<DefaultTesselatingDisplay<*>> {
    override fun getCategoryIdentifier() = TesselatingReiCompatClient.TESSELATING

    override fun getIcon() = EntryStacks.of(ModBlocks.TESSELATING_LOOM)

    override fun getTitle() = Component.translatable("category.dimdoors.tesselating")

    override fun setupDisplay(display: DefaultTesselatingDisplay<*>, bounds: Rectangle): MutableList<Widget> {
        val startPoint = Point(bounds.centerX - 58, bounds.centerY - 27)
        val widgets = mutableListOf<Widget>()
        widgets.add(Widgets.createRecipeBase(bounds))
        widgets.add(
            Widgets.createArrow(Point(startPoint.x + 60, startPoint.y + 18))
                .animationDurationTicks(display.weavingTime.toDouble())
        )

        widgets.add(Widgets.createResultSlotBackground(Point(startPoint.x + 95, startPoint.y + 19)))
        val input = display.getInputIngredients(3, 3)
        val slots = mutableListOf<Slot>()
        for (y in 0..2) for (x in 0..2) slots.add(
            Widgets.createSlot(
                Point(
                    startPoint.x + 1 + x * 18,
                    startPoint.y + 1 + y * 18
                )
            ).markInput()
        )
        input.forEach { ingredient -> slots[ingredient.index].entries(ingredient.get()) }

        widgets.addAll(slots)
        widgets.add(
            Widgets.createSlot(Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0))
                .disableBackground().markOutput()
        )

        if (display.isShapeless) {
            widgets.add(Widgets.createShapelessIcon(bounds))
        }
        return widgets
    }

    override fun getDisplayMerger(): DisplayMerger<DefaultTesselatingDisplay<*>> {
        return object : DisplayMerger<DefaultTesselatingDisplay<*>> {
            override fun canMerge(first: DefaultTesselatingDisplay<*>, second: DefaultTesselatingDisplay<*>): Boolean {
                if (first.categoryIdentifier != second.categoryIdentifier) return false
                if (!equals(
                        first.getOrganisedInputEntries<AbstractContainerMenu>(3, 3),
                        second.getOrganisedInputEntries<AbstractContainerMenu>(3, 3)
                    )
                ) return false
                if (!equals(first.outputEntries, second.outputEntries)) return false
                if (first.isShapeless != second.isShapeless) return false
                if (first.width != second.width) return false
                if (first.height != second.height) return false
                return true
            }

            override fun hashOf(display: DefaultTesselatingDisplay<*>): Int {
                return display.categoryIdentifier
                    .hashCode() * 31 * 31 * 31 + display.getOrganisedInputEntries<AbstractContainerMenu?>(3, 3)
                    .hashCode() * 31 * 31 + display.outputEntries.hashCode()
            }

            fun equals(l1: MutableList<EntryIngredient>, l2: MutableList<EntryIngredient>): Boolean {
                if (l1.size != l2.size) return false
                val it1 = l1.iterator()
                val it2 = l2.iterator()
                while (it1.hasNext() && it2.hasNext()) {
                    if (it1.next() != it2.next()) return false
                }
                return true
            }
        }
    }
}