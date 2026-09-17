package org.dimdev.dimdoors.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

public abstract class SimpleTesselatingRecipeBuilder<T extends TesselatingRecipe, V> extends TesselatingRecipeBuilder<T, V> {
    private final ItemStack result;

    public SimpleTesselatingRecipeBuilder(ItemStack output) {
        this.result = output;
    }

    public Item getResult() {
        return this.result.getItem();
    }



    @Override
    protected T createResult(V extraValue) {
        return createResult(result, extraValue);
    }

    abstract protected T createResult(ItemStack stack, V extraValue);
}