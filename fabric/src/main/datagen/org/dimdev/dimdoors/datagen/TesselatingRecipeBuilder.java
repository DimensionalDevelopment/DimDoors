package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class TesselatingRecipeBuilder<T extends TesselatingRecipe, V> implements RecipeBuilder {
    protected int weavingTime = 200;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable protected String group;

    public @NotNull TesselatingRecipeBuilder unlockedBy(String string, Criterion criterionConditions) {
        this.criteria.put(string, criterionConditions);
        return this;
    }

    public @NotNull TesselatingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public TesselatingRecipeBuilder weavingTime(int weavingTime) {
        this.weavingTime = weavingTime;
        return this;
    }

    protected V ensureValid(Identifier id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }

        return null;
    }

    public void save(RecipeOutput recipeOutput, Identifier id) {
        var extraValue = this.ensureValid(id);
        Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        Objects.requireNonNull(builder);
        this.criteria.forEach(builder::addCriterion);

        recipeOutput.accept(id, createResult(extraValue), builder.build(id.withPrefix("recipes/tesselating/")));
    }

    protected abstract T createResult(V extraValue);
}
