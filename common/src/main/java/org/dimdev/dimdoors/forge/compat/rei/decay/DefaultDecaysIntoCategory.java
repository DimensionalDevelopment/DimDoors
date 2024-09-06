package org.dimdev.dimdoors.forge.compat.rei.decay;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.forge.compat.rei.TesselatingReiCompatClient;
import org.dimdev.dimdoors.forge.world.decay.DecayResult;

import java.util.List;

public class DefaultDecaysIntoCategory implements DisplayCategory<DefaultDecaysIntoDisplay> {
    @Override
    public CategoryIdentifier<? extends DefaultDecaysIntoDisplay> getCategoryIdentifier() {
        return TesselatingReiCompatClient.DECAYS_INTO;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.dimdoors.decays_into");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.UNRAVELED_FENCE.get());
    }

    @Override
    public List<Widget> setupDisplay(DefaultDecaysIntoDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        List<EntryStack<?>> input = display.getDecayEntries();
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 19, startPoint.y + 19)).entry(DefaultDecaysIntoDisplay.toEntryStack(DecayResult.defaultProduces(display.key()))).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(input).disableBackground().markOutput());
        return widgets;
    }

}
