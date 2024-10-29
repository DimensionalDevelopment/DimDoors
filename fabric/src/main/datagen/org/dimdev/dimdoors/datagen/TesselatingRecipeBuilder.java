package org.dimdev.dimdoors.datagen;

import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class TesselatingRecipeBuilder implements RecipeBuilder {
    protected int weavingTime = 200;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable protected String group;

    public TesselatingRecipeBuilder unlockedBy(String string, Criterion criterionConditions) {
        this.criteria.put(string, criterionConditions);
        return this;
    }

    public TesselatingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public TesselatingRecipeBuilder weavingTime(int weavingTime) {
        this.weavingTime = weavingTime;
        return this;
    }

    protected void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = id.withPrefix("tesselating/");
        this.ensureValid(id);
        Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        Objects.requireNonNull(builder);
        this.criteria.forEach(builder::addCriterion);
        recipeOutput.accept(createResult(id, builder));
    }

    protected abstract  <T extends TesselatingRecipeResult> T createResult(ResourceLocation id, Advancement.Builder holder);

    protected abstract static class TesselatingRecipeResult implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final AdvancementHolder advancement;
        private final int weavingTime;

        protected TesselatingRecipeResult(ResourceLocation id, String group, AdvancementHolder advancement, int weavingTime) {
            this.id = id;
            this.group = group;
            this.advancement = advancement;
            this.weavingTime = weavingTime;
        }

        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            json.addProperty("weaving_time", this.weavingTime);
        }

        public ResourceLocation id() {
            return this.id;
        }

        public AdvancementHolder advancement() {
            return this.advancement;
        }
    }
}
