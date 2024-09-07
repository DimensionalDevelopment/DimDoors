package org.dimdev.dimdoors.compat.rei.tesselating;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class DefaultTesselatingCategory implements DisplayCategory<DefaultTesselatingDisplay<?>> {
    @Override
    public CategoryIdentifier<? extends DefaultTesselatingDisplay<?>> getCategoryIdentifier() {
        return TesselatingReiCompatClient.TESSELATING;
    }
    
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.TESSELATING_LOOM.get());
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("category.dimdoors.tesselating");
    }
    
    @Override
    public List<Widget> setupDisplay(DefaultTesselatingDisplay<?> display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)).animationDurationTicks(display.getWeavingTime()));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        List<InputIngredient<EntryStack<?>>> input = display.getInputIngredients(3, 3);
        List<Slot> slots = Lists.newArrayList();
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                slots.add(Widgets.createSlot(new Point(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18)).markInput());
        for (InputIngredient<EntryStack<?>> ingredient : input) {
            slots.get(ingredient.getIndex()).entries(ingredient.get());
        }
        widgets.addAll(slots);
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        if (display.isShapeless()) {
            widgets.add(Widgets.createShapelessIcon(bounds));
        }
        return widgets;
    }
    
    @Override
    @Nullable
    public DisplayMerger<DefaultTesselatingDisplay<?>> getDisplayMerger() {
        return new DisplayMerger<>() {
            @Override
            public boolean canMerge(DefaultTesselatingDisplay<?> first, DefaultTesselatingDisplay<?> second) {
                if (!first.getCategoryIdentifier().equals(second.getCategoryIdentifier())) return false;
                if (!equals(first.getOrganisedInputEntries(3, 3), second.getOrganisedInputEntries(3, 3))) return false;
                if (!equals(first.getOutputEntries(), second.getOutputEntries())) return false;
                if (first.isShapeless() != second.isShapeless()) return false;
                if (first.getWidth() != second.getWidth()) return false;
                if (first.getHeight() != second.getHeight()) return false;
                return true;
            }
            
            @Override
            public int hashOf(DefaultTesselatingDisplay<?> display) {
                return display.getCategoryIdentifier().hashCode() * 31 * 31 * 31 + display.getOrganisedInputEntries(3, 3).hashCode() * 31 * 31 + display.getOutputEntries().hashCode();
            }
            
            private boolean equals(List<EntryIngredient> l1, List<EntryIngredient> l2) {
                if (l1.size() != l2.size()) return false;
                Iterator<EntryIngredient> it1 = l1.iterator();
                Iterator<EntryIngredient> it2 = l2.iterator();
                while (it1.hasNext() && it2.hasNext()) {
                    if (!it1.next().equals(it2.next())) return false;
                }
                return true;
            }
        };
    }
}