package org.dimdev.dimdoors.compat.rei.decay;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient;

import java.util.List;

public class DefaultDecaysIntoCategory implements DisplayCategory<DecayPatternDisplay> {
    @Override
    public CategoryIdentifier<DecayPatternDisplay> getCategoryIdentifier() {
        return TesselatingReiCompatClient.DECAYS_INTO;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.dimdoors.decays_into");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.DRIFTWOOD_FENCE);
    }

    @Override
    public List<Widget> setupDisplay(DecayPatternDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 19, startPoint.y + 19)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

}
