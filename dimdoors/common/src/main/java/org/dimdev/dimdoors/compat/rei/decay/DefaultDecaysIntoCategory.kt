package org.dimdev.dimdoors.compat.rei.decay

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient

class DefaultDecaysIntoCategory : DisplayCategory<DecayPatternDisplay> {
    override fun getCategoryIdentifier() = TesselatingReiCompatClient.DECAYS_INTO

    override fun getTitle(): MutableComponent = Component.translatable("category.dimdoors.decays_into")

    override fun getIcon(): EntryStack<ItemStack> = EntryStacks.of(ModBlocks.DRIFTWOOD_FENCE)

    override fun setupDisplay(display: DecayPatternDisplay, bounds: Rectangle): MutableList<Widget> {
        val startPoint = Point(bounds.centerX - 58, bounds.centerY - 27)
        val widgets = mutableListOf<Widget>()
        widgets.add(Widgets.createRecipeBase(bounds))
        widgets.add(Widgets.createArrow(Point(startPoint.x + 60, startPoint.y + 18)))
        widgets.add(Widgets.createResultSlotBackground(Point(startPoint.x + 95, startPoint.y + 19)))
        widgets.add(Widgets.createSlot(Point(startPoint.x + 19, startPoint.y + 19)).entries(display.inputEntries[0]).markInput())
        widgets.add(Widgets.createSlot(Point(startPoint.x + 95, startPoint.y + 19)).entries(display.outputEntries[0]).disableBackground().markOutput())
        return widgets
    }
}
