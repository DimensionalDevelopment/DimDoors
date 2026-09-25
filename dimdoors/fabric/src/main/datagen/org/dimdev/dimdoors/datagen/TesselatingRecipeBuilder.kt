package org.dimdev.dimdoors.datagen

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.recipe.TesselatingRecipe

abstract class TesselatingRecipeBuilder<T : TesselatingRecipe, V> : RecipeBuilder {
    protected var weavingTime: Int = 200
    protected val criteria = mutableMapOf<String, Criterion<*>>()
    protected var group: String? = null

    override fun unlockedBy(string: String, criterionConditions: Criterion<*>): TesselatingRecipeBuilder<T, V> {
        this.criteria[string] = criterionConditions
        return this
    }

    override fun group(string: String?): TesselatingRecipeBuilder<T, V> {
        this.group = string
        return this
    }

    fun weavingTime(weavingTime: Int): TesselatingRecipeBuilder<T, V> {
        this.weavingTime = weavingTime
        return this
    }

    protected open fun ensureValid(id: ResourceLocation): V? {
        check(!this.criteria.isEmpty()) { "No way of obtaining recipe $id" }

        return null
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val extraValue = this.ensureValid(id)!!
        val builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR)
        requireNotNull(builder)
        this.criteria.forEach { (key, criterion) -> builder.addCriterion(key, criterion) }

        recipeOutput.accept(id, createResult(extraValue), builder.build(id.withPrefix("recipes/tesselating/")))
    }

    protected abstract fun createResult(extraValue: V): T
}
