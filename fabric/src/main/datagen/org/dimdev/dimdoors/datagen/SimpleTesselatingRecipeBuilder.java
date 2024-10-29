package org.dimdev.dimdoors.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

public abstract class SimpleTesselatingRecipeBuilder extends TesselatingRecipeBuilder {
    private final ItemLike result;
    private final int count;

    public SimpleTesselatingRecipeBuilder(ItemLike output, int outputCount) {
        this.result = output.asItem();
        this.count = outputCount;
    }

    public Item getResult() {
        return this.result.asItem();
    }

    @Override
    protected <T extends TesselatingRecipe> T createResult() {
        return createResult(result, count);
    }

    abstract protected <T extends TesselatingRecipe> T createResult(ItemLike itemLike, int count);
}