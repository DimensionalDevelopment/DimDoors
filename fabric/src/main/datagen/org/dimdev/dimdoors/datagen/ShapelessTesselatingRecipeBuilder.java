package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;

import java.util.List;

public class ShapelessTesselatingRecipeBuilder extends SimpleTesselatingRecipeBuilder<TesselatingShapelessRecipe, NonNullList<Ingredient>> {
    private final List<Ingredient> ingredients = Lists.newArrayList();

    public ShapelessTesselatingRecipeBuilder(ItemStack result) {
        super(result);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static ShapelessTesselatingRecipeBuilder shapeless(ItemLike result) {
        return shapeless(result, 1);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static ShapelessTesselatingRecipeBuilder shapeless(ItemLike result, int count) {
        var stack = new ItemStack(result, count);
        return new ShapelessTesselatingRecipeBuilder(stack);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public ShapelessTesselatingRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public ShapelessTesselatingRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public ShapelessTesselatingRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    /**
     * Adds an ingredient.
     */
    public ShapelessTesselatingRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public ShapelessTesselatingRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    @Override
    protected TesselatingShapelessRecipe createResult(ItemStack result, NonNullList<Ingredient> ingredients) {
        return new TesselatingShapelessRecipe(this.group == null ? "" : this.group, result, ingredients, weavingTime);
    }
}


