package org.dimdev.dimdoors.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.compat.rei.decay.DecayPatternDisplay;
import org.dimdev.dimdoors.compat.rei.decay.DefaultDecaysIntoCategory;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingCategory;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingDisplay;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapedDisplay;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapelessDisplay;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;
import org.dimdev.dimdoors.screen.TessellatingContainer;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class TesselatingReiCompatClient implements REIClientPlugin {
    public static final CategoryIdentifier<? extends DefaultTesselatingDisplay<?>> TESSELATING = CategoryIdentifier.of("dimdoors", "tesselating");
    public static final CategoryIdentifier<DecayPatternDisplay> DECAYS_INTO = CategoryIdentifier.of("dimdoors", "decays_into");
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultTesselatingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.TESSELATING_LOOM.get())));
        registry.add(new DefaultDecaysIntoCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.UNRAVELLED_FABRIC.get())));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ShapedTesselatingRecipe.class, ModRecipeTypes.TESSELATING.get(), (Function<RecipeHolder<ShapedTesselatingRecipe>, Display>) DefaultTesselatingShapedDisplay::new);
        registry.registerRecipeFiller(TesselatingShapelessRecipe.class, ModRecipeTypes.TESSELATING.get(), (Function<RecipeHolder<TesselatingShapelessRecipe>, Display>) DefaultTesselatingShapelessDisplay::new);

        var registryAccess = DimensionalDoors.getServer().registryAccess();

        var list = Decay.DecayLoader.getPatterns()
                .entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .flatMap(a -> DecayPatternDisplay.list(a, registryAccess).stream())
                .toList();
        list.forEach(registry::add);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(90, 35, 22, 15), TesselatingLoomScreen.class, TESSELATING);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(TessellatingContainer.class, TESSELATING, new SimpleTransferHandler.IntRange(1, 10)));
    }
}
