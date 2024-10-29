package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class ShapelessTesselatingRecipeBuilder extends SimpleTesselatingRecipeBuilder {
    private final List<Ingredient> ingredients = Lists.newArrayList();

    public ShapelessTesselatingRecipeBuilder(ItemLike result, int count) {
        super(result, count);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static ShapelessTesselatingRecipeBuilder shapeless(ItemLike result) {
        return new ShapelessTesselatingRecipeBuilder(result, 1);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static ShapelessTesselatingRecipeBuilder shapeless(ItemLike result, int count) {
        return new ShapelessTesselatingRecipeBuilder(result, count);
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
    protected Result createResult(ResourceLocation id, Advancement.Builder builder, ItemLike result, int count) {
        return new Result(id, result, count, this.group == null ? "" : this.group, this.ingredients, builder.build(id), weavingTime);
    }

    public static class Result extends SimpleResult {
        private final List<Ingredient> ingredients;

        public Result(ResourceLocation id, ItemLike result, int count, String group, List<Ingredient> ingredients, AdvancementHolder advancement, int weavingTime) {
            super(id, result, count, group, advancement, weavingTime);
            this.ingredients = ingredients;
        }

        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);

            JsonArray jsonArray = new JsonArray();

            for (Ingredient ingredient : this.ingredients) {
                jsonArray.add(ingredient.toJson(false));
            }

            json.add("ingredients", jsonArray);
        }

        public RecipeSerializer<?> type() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }
    }
}


