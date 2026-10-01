package org.dimdev.dimdoors.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen
import org.dimdev.dimdoors.compat.jei.decay.DecayCategory
import org.dimdev.dimdoors.compat.jei.decay.DecayRecipes.decays
import org.dimdev.dimdoors.compat.jei.tesselating.DimDoorsRecipes
import org.dimdev.dimdoors.compat.jei.tesselating.TesselatingRecipeCategory
import org.dimdev.dimdoors.recipe.TesselatingRecipe

@JeiPlugin
class DimDoorsJeiCompatClient : IModPlugin {
    private var tesselatingCategory: TesselatingRecipeCategory? = null
    private var decayCategory: DecayCategory? = null


    override fun getPluginUid(): ResourceLocation {
        return id
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(
            TesselatingRecipeCategory(
                registration.jeiHelpers.guiHelper
            ).also { this.tesselatingCategory = it })
        registration.addRecipeCategories(
            DecayCategory(
                registration.jeiHelpers.guiHelper
            ).also { this.decayCategory = it })
    }



    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        super.registerVanillaCategoryExtensions(registration)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val ingredientManager = registration.getIngredientManager()
        val dimDoorsRecipes = DimDoorsRecipes(ingredientManager)

        val tesselatingRecipes = dimDoorsRecipes.getTesselating(tesselatingCategory)
        val handledTesselatingRecipes: MutableList<RecipeHolder<TesselatingRecipe?>?> = tesselatingRecipes.get(true)!!

        registration.addRecipes(
            ModRecipeTypes.TESSELATING,
            handledTesselatingRecipes
        )
        registration.addRecipes(ModRecipeTypes.DECAY, decays)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea<TesselatingLoomScreen?>(
            TesselatingLoomScreen::class.java,
            88,
            32,
            28,
            23,
            ModRecipeTypes.TESSELATING
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ModBlocks.TESSELATING_LOOM, ModRecipeTypes.TESSELATING)
        registration.addRecipeCatalyst(ModBlocks.UNRAVELLED_FABRIC, ModRecipeTypes.DECAY)
    }

    companion object {
        private val id = DimensionalDoors.id("jei")
    }
}
