package org.dimdev.dimdoors.mixin.forge;

import net.minecraft.world.Container;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShapedTesselatingRecipe.class)
public abstract class TessellatingRecipeMixin implements IShapedRecipe<Container> {
    @Unique
    private ShapedTesselatingRecipe self() {
        return (ShapedTesselatingRecipe) (Object) this;
    }

    @Override
    public int getRecipeWidth() {
        return self().getWidth();
    }

    @Override
    public int getRecipeHeight() {
        return self().getHeight();
    }
}
