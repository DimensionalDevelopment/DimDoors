package org.dimdev.dimdoors.forge.compat.rei.decay;

import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.forge.compat.rei.TesselatingReiCompatClient;
import org.dimdev.dimdoors.forge.world.decay.DecayPattern;

import java.util.List;
import java.util.Objects;

public record DefaultDecaysIntoDisplay<S>(S key, List<EntryIngredient> ingredients) implements Display {

    public static <T> DefaultDecaysIntoDisplay<T> of(T object, List<DecayPattern> patterns) {
        var ingredients = patterns.stream().map(pattern -> pattern.willBecome(object)).filter(Objects::nonNull).map(DefaultDecaysIntoDisplay::toEntryStack).map(EntryIngredient::of).toList();

        return new DefaultDecaysIntoDisplay<T>(object, ingredients);
    }

    public static EntryStack<?> toEntryStack(Object object) {
        if(object instanceof ItemStack stack) return EntryStack.of(VanillaEntryTypes.ITEM, stack);
        else if(object instanceof FluidStack stack) return EntryStack.of(VanillaEntryTypes.FLUID, stack);
        else return EntryStack.empty();
    }


    @Override
    public List<EntryIngredient> getInputEntries() {
        return ingredients;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return ingredients;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TesselatingReiCompatClient.DECAYS_INTO;
    }

    public List<EntryStack<?>> getDecayEntries() {
        return ingredients.stream().flatMap(a -> a.stream()).toList();
    }
}
