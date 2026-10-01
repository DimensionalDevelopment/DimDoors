package org.dimdev.dimdoors.compat.jei.tesselating

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.serialization.Codec
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.builder.ITooltipBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder
import mezz.jei.api.helpers.ICodecHelper
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.IRecipeManager
import mezz.jei.api.recipe.category.AbstractRecipeCategory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.compat.jei.ModRecipeTypes
import org.dimdev.dimdoors.compat.jei.tesselating.DimDoorsRecipes.TesselatingRecipeExtension
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe

class TesselatingRecipeCategory(private val guiHelper: IGuiHelper) : AbstractRecipeCategory<RecipeHolder<TesselatingRecipe>>(
        ModRecipeTypes.TESSELATING,
        Component.translatable("category.dimdoors.tesselating"),
        guiHelper.createDrawableItemLike(ModBlocks.TESSELATING_LOOM),
        width,
        height
    ), IExtendableRksRecipeCategory {
    private val craftingGridHelper: ICraftingGridHelper = guiHelper.createCraftingGridHelper()
    private val extendableHelper = TesselatingExtensionHelper()

    init {

        addExtension(TesselatingShapelessRecipe::class.java, TesselatingRecipeExtension())
        addExtension<ShapedTesselatingRecipe>(ShapedTesselatingRecipe::class.java, TesselatingRecipeExtension())
    }

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        focuses: IFocusGroup
    ) {
        val recipeExtension = this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        recipeExtension.setRecipe(recipeHolder, builder, craftingGridHelper, focuses)
    }

    override fun onDisplayedIngredientsUpdate(
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        recipeSlots: MutableList<IRecipeSlotDrawable>,
        focuses: IFocusGroup
    ) {
        val recipeExtension = this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        recipeExtension.onDisplayedIngredientsUpdate(recipeHolder, recipeSlots, focuses)
    }

    override fun createRecipeExtras(
        builder: IRecipeExtrasBuilder,
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        focuses: IFocusGroup
    ) {
        val recipeExtension =
            this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        recipeExtension.createRecipeExtras(recipeHolder, builder, craftingGridHelper, focuses)
    }

    override fun draw(
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val extension = this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        val recipeWidth = this.width
        val recipeHeight = this.height
        extension.drawInfo(recipeHolder, recipeWidth, recipeHeight, guiGraphics, mouseX, mouseY)

        val recipeArrow = guiHelper.recipeArrow
        recipeArrow.draw(guiGraphics, 61, (Companion.height - recipeArrow.height) / 2)
    }

    override fun getTooltip(
        tooltip: ITooltipBuilder,
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        recipeSlotsView: IRecipeSlotsView,
        mouseX: Double,
        mouseY: Double
    ) {
        val extension: ITesselatingCategoryExtension<TesselatingRecipe> =
            this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        extension.getTooltip(tooltip, recipeHolder, mouseX, mouseY)
    }


    override fun getTooltipStrings(
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        recipeSlotsView: IRecipeSlotsView,
        mouseX: Double,
        mouseY: Double
    ): MutableList<Component> {
        val extension =
            this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        return extension.getTooltipStrings(recipeHolder, mouseX, mouseY)
    }

    override fun handleInput(
        recipeHolder: RecipeHolder<TesselatingRecipe>,
        mouseX: Double,
        mouseY: Double,
        input: InputConstants.Key
    ): Boolean {
        val extension: ITesselatingCategoryExtension<TesselatingRecipe> =
            this.extendableHelper.getRecipeExtension<TesselatingRecipe>(recipeHolder)
        return extension.handleInput(recipeHolder, mouseX, mouseY, input)
    }

    override fun isHandled(recipeHolder: RecipeHolder<TesselatingRecipe>): Boolean {
        return this.extendableHelper.getOptionalRecipeExtension(recipeHolder) != null
    }

    override fun <R : TesselatingRecipe> addExtension(recipeClass: Class<out R>, extension: ITesselatingCategoryExtension<R>
    ) {
        extendableHelper.addRecipeExtension(recipeClass, extension)
    }

    override fun getRegistryName(recipeHolder: RecipeHolder<TesselatingRecipe>) = this.extendableHelper.getOptionalRecipeExtension<TesselatingRecipe>(recipeHolder)?.getRegistryName(recipeHolder) ?: recipeHolder.id()

    override fun getCodec(
        codecHelper: ICodecHelper,
        recipeManager: IRecipeManager
    ): Codec<RecipeHolder<TesselatingRecipe>> {
        return codecHelper.getRecipeHolderCodec()
    }

    companion object {
        const val width: Int = 116
        const val height: Int = 54
    }
}
