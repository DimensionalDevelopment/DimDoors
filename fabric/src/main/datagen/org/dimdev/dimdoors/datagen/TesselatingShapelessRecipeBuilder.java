package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TesselatingShapelessRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
    @Nullable
    private String group;

    private int weavingTime = 200;


    public TesselatingShapelessRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static TesselatingShapelessRecipeBuilder shapeless(ItemLike result) {
        return new TesselatingShapelessRecipeBuilder(result, 1);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static TesselatingShapelessRecipeBuilder shapeless(ItemLike result, int count) {
        return new TesselatingShapelessRecipeBuilder(result, count);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public TesselatingShapelessRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public TesselatingShapelessRecipeBuilder weavingTime(int weavingTime) {
        this.weavingTime = weavingTime;
        return this;
    }

    /**
     * Adds an ingredient of the given item.
     */
    public TesselatingShapelessRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public TesselatingShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    /**
     * Adds an ingredient.
     */
    public TesselatingShapelessRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public TesselatingShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    @Override
    public TesselatingShapelessRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        this.criteria.put(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public TesselatingShapelessRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        TesselatingShapelessRecipe shapelessRecipe = new TesselatingShapelessRecipe((String)Objects.requireNonNullElse(this.group, ""), new ItemStack(this.result, this.count), this.ingredients, weavingTime);
        recipeOutput.accept(id, shapelessRecipe, builder.build(id.withPrefix("recipes/tesselating/")));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}


