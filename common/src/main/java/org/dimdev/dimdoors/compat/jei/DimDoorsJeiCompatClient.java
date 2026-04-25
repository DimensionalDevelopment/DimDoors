package org.dimdev.dimdoors.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.compat.jei.decay.DecayCategory;
import org.dimdev.dimdoors.compat.jei.decay.DecayRecipes;
import org.dimdev.dimdoors.compat.jei.tesselating.DimDoorsRecipes;
import org.dimdev.dimdoors.compat.jei.tesselating.TesselatingRecipeCategory;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.compat.jei.ModRecipeTypes.DECAY;
import static org.dimdev.dimdoors.compat.jei.ModRecipeTypes.TESSELATING;

@JeiPlugin
public class DimDoorsJeiCompatClient implements IModPlugin {
    private static final ResourceLocation id = DimensionalDoors.id("jei");
    @Nullable private TesselatingRecipeCategory tesselatingCategory;
    @Nullable private DecayCategory decayCategory;


    @Override
    public ResourceLocation getPluginUid() {
        return id;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(this.tesselatingCategory = new TesselatingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(this.decayCategory = new DecayCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IModPlugin.super.registerVanillaCategoryExtensions(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        DimDoorsRecipes dimDoorsRecipes = new DimDoorsRecipes(ingredientManager);

        var tesselatingRecipes = dimDoorsRecipes.getTesselating(tesselatingCategory);
        var handledTesselatingRecipes = tesselatingRecipes.get(true);

        registration.addRecipes(TESSELATING, handledTesselatingRecipes);
        registration.addRecipes(DECAY, DecayRecipes.getDecays());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TesselatingLoomScreen.class, 88, 32, 28, 23, TESSELATING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.TESSELATING_LOOM.get(), TESSELATING);
        registration.addRecipeCatalyst(ModBlocks.UNRAVELLED_FABRIC.get(), DECAY);
    }
}
