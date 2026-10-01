package org.dimdev.dimdoors.client

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent
import org.dimdev.dimdoors.mixin.client.RecipeBookCategoriesAccessor

object RecipeBookManager {
    private val TYPE_CATEGORIES = mutableMapOf<RecipeBookType, MutableList<RecipeBookCategories>>()
    private val RECIPE_CATEGORY_LOOKUPS = mutableMapOf<RecipeType<*>, (RecipeHolder<*>) -> RecipeBookCategories>()
    private var initialized = false

    @JvmStatic
    fun <T : Recipe<*>> findCategories(type: RecipeType<T>, recipe: RecipeHolder<T>): RecipeBookCategories? =
        RECIPE_CATEGORY_LOOKUPS[type]?.invoke(recipe)

    @JvmStatic
    fun getCustomCategoriesOrEmpty(recipeBookType: RecipeBookType): MutableList<RecipeBookCategories> =
        TYPE_CATEGORIES.getOrDefault(recipeBookType, mutableListOf())

    @JvmStatic
    fun init() {
        if (initialized) return

        initialized = true
        ModRecipeBookGroups.init()

        val aggregateCategories = HashMap(RecipeBookCategoriesAccessor.aggregateCategories())
        val event = RegisterRecipeBookCategoriesEvent(
            { category, others -> aggregateCategories[category] = others },
            { type, categories -> TYPE_CATEGORIES[type] = categories },
            { type, finder -> RECIPE_CATEGORY_LOOKUPS[type] = finder })
        RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(event)
        RecipeBookCategoriesAccessor.setAggregateCategories(aggregateCategories)
    }
}
