package org.dimdev.dimdoors.mixin.forge;

import net.minecraft.world.Container;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TesselatingRecipe.class)
public abstract class TessellatingRecipeMixin implements IShapedRecipe<Container> {
    @Unique
    private TesselatingRecipe self() {
        return (TesselatingRecipe) (Object) this;
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
