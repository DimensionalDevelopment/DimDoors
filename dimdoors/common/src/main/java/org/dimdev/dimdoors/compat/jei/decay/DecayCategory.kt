package org.dimdev.dimdoors.compat.jei.decay

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.AbstractRecipeCategory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.compat.decay.DecayDisplayData
import org.dimdev.dimdoors.compat.jei.ModRecipeTypes

class DecayCategory(private val guiHelper: IGuiHelper) : AbstractRecipeCategory<DecayDisplayData>(
    ModRecipeTypes.DECAY,
    Component.translatable("category.dimdoors.decays_into"),
    guiHelper.createDrawableItemLike(ModBlocks.UNRAVELLED_FABRIC),
    WIDTH,
    HEIGHT
) {
    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DecayDisplayData, focuses: IFocusGroup) {
        DecayJeiUtil.addInput(builder.addInputSlot(19, 19).setStandardSlotBackground(), recipe.input)

        for (i in recipe.outputs.indices) {
            val x = 94 + (i % 2) * 20
            val y = 10 + (i / 2) * 20
            DecayJeiUtil.addOutput(builder.addOutputSlot(x, y).setStandardSlotBackground(), recipe.outputs[i])
        }
    }

    override fun draw(
        recipe: DecayDisplayData,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val recipeArrow = guiHelper.recipeArrow
        recipeArrow.draw(guiGraphics, 60, (HEIGHT - recipeArrow.height) / 2)
    }

    companion object {
        const val WIDTH: Int = 134
        const val HEIGHT: Int = 54
    }
}
