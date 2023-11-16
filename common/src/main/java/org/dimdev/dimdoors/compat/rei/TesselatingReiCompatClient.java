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
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.screen.TessellatingContainer;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class TesselatingReiCompatClient implements REIClientPlugin {
    public static final CategoryIdentifier<? extends DefaultTesselatingDisplay<?>> TESSELATING = CategoryIdentifier.of("dimdoors", "tesselating");
    @Override
    public void registerCategories(CategoryRegistry registry) {

        registry.add(new DefaultTesselatingCategory(), new Consumer<CategoryRegistry.CategoryConfiguration<DefaultTesselatingDisplay<?>>>() {
            @Override
            public void accept(CategoryRegistry.CategoryConfiguration<DefaultTesselatingDisplay<?>> configuration) {
                configuration.addWorkstations(EntryStacks.of(ModBlocks.TESSELATING_LOOM.get()));
            }
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(TesselatingRecipe.class, DefaultTesselatingDisplay::of);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(137, 29, 10, 13), TesselatingLoomScreen.class, TESSELATING);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(TessellatingContainer.class, TESSELATING, new SimpleTransferHandler.IntRange(1, 10)));
    }



    @Override
    public double getPriority() {
        return -300;
    }
}
