package org.dimdev.dimdoors.api.util

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimcore.api.util.SimpleEvent
import org.dimdev.dimcore.api.util.SimpleEvent.Companion.consumerLoop
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

data class RegisterRecipeBookCategoriesEvent(
    val categoryAggregateCategory: (RecipeBookCategories, MutableList<RecipeBookCategories>) -> Unit,
    val bookCategories: (RecipeBookType, MutableList<RecipeBookCategories>) -> Unit,
    val recipeCategoryFinder: (RecipeType<*>, (RecipeHolder<*>) -> RecipeBookCategories) -> Unit
) {
    fun registerAggregateCategory(category: RecipeBookCategories, other: MutableList<RecipeBookCategories>) =
        categoryAggregateCategory.invoke(category, other)

    fun registerBookCategories(type: RecipeBookType, categories: MutableList<RecipeBookCategories>) =
        bookCategories.invoke(type, categories)

    fun registerRecipeCategoryFinder(type: RecipeType<*>, categoriesFunction: (RecipeHolder<*>) -> RecipeBookCategories) =
        recipeCategoryFinder.invoke(type, categoriesFunction)

    companion object {
        val EVENT = consumerLoop<Consumer<RegisterRecipeBookCategoriesEvent>>()
    }
}
