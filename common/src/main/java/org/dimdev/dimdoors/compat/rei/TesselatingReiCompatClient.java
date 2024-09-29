package org.dimdev.dimdoors.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.compat.rei.decay.DefaultDecaysIntoCategory;
import org.dimdev.dimdoors.compat.rei.decay.DefaultDecaysIntoDisplay;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingCategory;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingDisplay;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapedDisplay;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapelessDisplay;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;
import org.dimdev.dimdoors.screen.TessellatingContainer;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecayPattern;

public class TesselatingReiCompatClient implements REIClientPlugin {
    public static final CategoryIdentifier<? extends DefaultTesselatingDisplay<?>> TESSELATING = CategoryIdentifier.of("dimdoors", "tesselating");
    public static final CategoryIdentifier<? extends DefaultDecaysIntoDisplay> DECAYS_INTO = CategoryIdentifier.of("dimdoors", "decays_into");
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultTesselatingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.TESSELATING_LOOM.get())));
        registry.add(new DefaultDecaysIntoCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.UNRAVELLED_FABRIC.get())));



//        registry.configure(DECAYS_INTO, config -> config.setQuickCraftingEnabledByDefault(false));
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        REIClientPlugin.super.registerEntries(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ShapedTesselatingRecipe.class, ModRecipeTypes.TESSELATING.get(), DefaultTesselatingDisplay::of);

        Decay.DecayLoader.getInstance().getBlockPatterns().forEach((block, patterns) -> {
            registry.add(DefaultDecaysIntoDisplay.of(block, patterns));
        });
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
