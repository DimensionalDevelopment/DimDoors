package org.dimdev.dimdoors.compat.jei.tesselating

import com.mojang.serialization.Codec
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.ICodecHelper
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.IRecipeManager
import mezz.jei.api.recipe.category.AbstractRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.compat.jei.ModRecipeTypes
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingRecipe

class TesselatingRecipeCategory(private val guiHelper: IGuiHelper) : AbstractRecipeCategory<RecipeHolder<TesselatingRecipe>>(
    ModRecipeTypes.TESSELATING,
    Component.translatable("category.dimdoors.tesselating"),
    guiHelper.createDrawableItemLike(ModBlocks.TESSELATING_LOOM),
    WIDTH,
    HEIGHT
) {
    private val craftingGridHelper = guiHelper.createCraftingGridHelper()

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipeHolder: RecipeHolder<TesselatingRecipe>, focuses: IFocusGroup) {
        val recipe = recipeHolder.value()
        val shaped = recipe as? ShapedTesselatingRecipe
        craftingGridHelper.createAndSetOutputs(builder, listOf(recipe.getResultItem(Minecraft.getInstance().level!!.registryAccess())))
        craftingGridHelper.createAndSetIngredients(builder, recipe.ingredients, shaped?.width ?: 0, shaped?.height ?: 0)
    }

    override fun draw(recipeHolder: RecipeHolder<TesselatingRecipe>, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        val arrow = guiHelper.recipeArrow
        arrow.draw(guiGraphics, 61, (HEIGHT - arrow.height) / 2)
    }

    override fun getRegistryName(recipeHolder: RecipeHolder<TesselatingRecipe>): ResourceLocation = recipeHolder.id()

    override fun getCodec(codecHelper: ICodecHelper, recipeManager: IRecipeManager): Codec<RecipeHolder<TesselatingRecipe>> = codecHelper.getRecipeHolderCodec()

    companion object {
        const val WIDTH = 116
        const val HEIGHT = 54
    }
}