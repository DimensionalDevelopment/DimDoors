package org.dimdev.dimdoors.compat.jei.tesselating;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;

import java.util.List;

import static org.dimdev.dimdoors.compat.jei.ModRecipeTypes.TESSELATING;

public class TesselatingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<TesselatingRecipe>> implements IExtendableRksRecipeCategory {
    public static final int width = 116;
    public static final int height = 54;


    private  final IGuiHelper guiHelper;
    private final ICraftingGridHelper craftingGridHelper;
    private final TesselatingExtensionHelper extendableHelper = new TesselatingExtensionHelper();

    public TesselatingRecipeCategory(IGuiHelper guiHelper) {

        super(TESSELATING,
                Component.translatable("category.dimdoors.tesselating"),
                guiHelper.createDrawableItemLike(ModBlocks.TESSELATING_LOOM),
                width,
                height);
        this.guiHelper = guiHelper;
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();

        addExtension(TesselatingShapelessRecipe.class, new DimDoorsRecipes.TesselatingRecipeExtension<>());
        addExtension(ShapedTesselatingRecipe.class, new DimDoorsRecipes.TesselatingRecipeExtension<>());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TesselatingRecipe> recipeHolder, IFocusGroup focuses) {
        var recipeExtension = this.extendableHelper.getRecipeExtension(recipeHolder);
        recipeExtension.setRecipe(recipeHolder, builder, craftingGridHelper, focuses);
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<TesselatingRecipe> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        var recipeExtension = this.extendableHelper.getRecipeExtension(recipeHolder);
        recipeExtension.onDisplayedIngredientsUpdate(recipeHolder, recipeSlots, focuses);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<TesselatingRecipe> recipeHolder, IFocusGroup focuses) {
        var recipeExtension = this.extendableHelper.getRecipeExtension(recipeHolder);
        recipeExtension.createRecipeExtras(recipeHolder, builder, craftingGridHelper, focuses);
    }

    @Override
    public void draw(RecipeHolder<TesselatingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var extension = this.extendableHelper.getRecipeExtension(recipeHolder);
        int recipeWidth = this.getWidth();
        int recipeHeight = this.getHeight();
        extension.drawInfo(recipeHolder, recipeWidth, recipeHeight, guiGraphics, mouseX, mouseY);

        IDrawableStatic recipeArrow = guiHelper.getRecipeArrow();
        recipeArrow.draw(guiGraphics, 61, (height - recipeArrow.getHeight()) / 2);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<TesselatingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var extension = this.extendableHelper.getRecipeExtension(recipeHolder);
        extension.getTooltip(tooltip, recipeHolder, mouseX, mouseY);
    }

    @SuppressWarnings({"removal"})
    @Override
    public List<Component> getTooltipStrings(RecipeHolder<TesselatingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var extension = this.extendableHelper.getRecipeExtension(recipeHolder);
        return extension.getTooltipStrings(recipeHolder, mouseX, mouseY);
    }

    @SuppressWarnings("removal")
    @Override
    public boolean handleInput(RecipeHolder<TesselatingRecipe> recipeHolder, double mouseX, double mouseY, InputConstants.Key input) {
        var extension = this.extendableHelper.getRecipeExtension(recipeHolder);
        return extension.handleInput(recipeHolder, mouseX, mouseY, input);
    }

    @Override
    public boolean isHandled(RecipeHolder<TesselatingRecipe> recipeHolder) {
        return this.extendableHelper.getOptionalRecipeExtension(recipeHolder)
                .isPresent();
    }

    @Override
    public <R extends TesselatingRecipe> void addExtension(Class<? extends R> recipeClass, ITesselatingCategoryExtension<R> extension) {
        extendableHelper.addRecipeExtension(recipeClass, extension);
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getRegistryName(RecipeHolder<TesselatingRecipe> recipeHolder) {
        return this.extendableHelper.getOptionalRecipeExtension(recipeHolder)
                .flatMap(extension -> extension.getRegistryName(recipeHolder))
                .orElseGet(recipeHolder::id);
    }

    @Override
    public Codec<RecipeHolder<TesselatingRecipe>> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return codecHelper.getRecipeHolderCodec();
    }
}
