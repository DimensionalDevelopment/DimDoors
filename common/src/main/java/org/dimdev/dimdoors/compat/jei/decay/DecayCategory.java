package org.dimdev.dimdoors.compat.jei.decay;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.compat.decay.DecayDisplayData;

import static org.dimdev.dimdoors.compat.jei.ModRecipeTypes.DECAY;

public class DecayCategory extends AbstractRecipeCategory<DecayDisplayData> {
    public static final int WIDTH = 134;
    public static final int HEIGHT = 54;

    private final IGuiHelper guiHelper;

    public DecayCategory(IGuiHelper guiHelper) {
        super(
                DECAY,
                Component.translatable("category.dimdoors.decays_into"),
                guiHelper.createDrawableItemLike(ModBlocks.UNRAVELLED_FABRIC.get()),
                WIDTH,
                HEIGHT
        );
        this.guiHelper = guiHelper;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DecayDisplayData recipe, IFocusGroup focuses) {
        DecayJeiUtil.addInput(builder.addInputSlot(19, 19).setStandardSlotBackground(), recipe.input());

        for (int i = 0; i < recipe.outputs().size(); i++) {
            int x = 94 + (i % 2) * 20;
            int y = 10 + (i / 2) * 20;
            DecayJeiUtil.addOutput(builder.addOutputSlot(x, y).setStandardSlotBackground(), recipe.outputs().get(i));
        }
    }

    @Override
    public void draw(DecayDisplayData recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableStatic recipeArrow = guiHelper.getRecipeArrow();
        recipeArrow.draw(guiGraphics, 60, (HEIGHT - recipeArrow.getHeight()) / 2);
    }
}
