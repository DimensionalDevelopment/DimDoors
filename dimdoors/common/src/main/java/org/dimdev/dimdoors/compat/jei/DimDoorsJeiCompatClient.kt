package org.dimdev.dimdoors.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen
import org.dimdev.dimdoors.compat.jei.decay.DecayCategory
import org.dimdev.dimdoors.compat.jei.decay.DecayRecipes.decays
import org.dimdev.dimdoors.compat.jei.tesselating.DimDoorsRecipes
import org.dimdev.dimdoors.compat.jei.tesselating.TesselatingRecipeCategory

@JeiPlugin
class DimDoorsJeiCompatClient : IModPlugin {
    private lateinit var tesselatingCategory: TesselatingRecipeCategory
    private lateinit var decayCategory: DecayCategory


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

    override fun registerRecipes(registration: IRecipeRegistration) {
        val ingredientManager = registration.ingredientManager
        val dimDoorsRecipes = DimDoorsRecipes(ingredientManager)

        val tesselatingRecipes = dimDoorsRecipes.getTesselating(tesselatingCategory)
        val handledTesselatingRecipes = tesselatingRecipes[true]!!

        registration.addRecipes(
            ModRecipeTypes.TESSELATING,
            handledTesselatingRecipes
        )
        registration.addRecipes(ModRecipeTypes.DECAY, decays)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(
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
