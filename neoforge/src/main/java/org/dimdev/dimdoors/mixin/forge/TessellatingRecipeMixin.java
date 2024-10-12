package org.dimdev.dimdoors.mixin.forge;

import net.minecraft.world.item.crafting.CraftingInput;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShapedTesselatingRecipe.class)
public abstract class TessellatingRecipeMixin implements IShapedRecipe<CraftingInput> {
    @Unique
    private ShapedTesselatingRecipe self() {
        return (ShapedTesselatingRecipe) (Object) this;
    }

    @Override
    public int getWidth() {
        return self().getWidth();
    }

    @Override
    public int getHeight() {
        return self().getHeight();
    }
}
