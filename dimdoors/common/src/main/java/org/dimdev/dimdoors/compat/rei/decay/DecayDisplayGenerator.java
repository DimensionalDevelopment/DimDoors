package org.dimdev.dimdoors.compat.rei.decay;

import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.server.MinecraftServer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public final class DecayDisplayGenerator implements DynamicDisplayGenerator<DecayPatternDisplay> {
    @Override
    public Optional<List<DecayPatternDisplay>> getRecipeFor(EntryStack<?> entry) {
        return Optional.of(filter(entry, false));
    }

    @Override
    public Optional<List<DecayPatternDisplay>> getUsageFor(EntryStack<?> entry) {
        return Optional.of(filter(entry, true));
    }

    @Override
    public Optional<List<DecayPatternDisplay>> generate(ViewSearchBuilder builder) {
        LinkedHashSet<DecayPatternDisplay> displays = new LinkedHashSet<>(getDisplays());

        if (!builder.getUsagesFor().isEmpty()) {
            displays.removeIf(display -> builder.getUsagesFor().stream().noneMatch(entry -> matches(display.getInputEntries(), entry)));
        }

        if (!builder.getRecipesFor().isEmpty()) {
            displays.removeIf(display -> builder.getRecipesFor().stream().noneMatch(entry -> matches(display.getOutputEntries(), entry)));
        }

        return Optional.of(List.copyOf(displays));
    }

    private static List<DecayPatternDisplay> filter(EntryStack<?> entry, boolean usage) {
        return getDisplays().stream()
                .filter(display -> matches(usage ? display.getInputEntries() : display.getOutputEntries(), entry))
                .toList();
    }

    private static boolean matches(List<EntryIngredient> ingredients, EntryStack<?> entry) {
        return ingredients.stream()
                .flatMap(Collection::stream)
                .anyMatch(stack -> EntryStacks.equalsFuzzy(stack, entry));
    }

    private static List<DecayPatternDisplay> getDisplays() {
        MinecraftServer server = DimensionalDoors.getServer();
        if (server == null) {
            return List.of();
        }

        return Decay.DecayLoader.getPatterns().values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .flatMap(pattern -> DecayPatternDisplay.list(pattern, server.registryAccess()).stream())
                .toList();
    }
}
