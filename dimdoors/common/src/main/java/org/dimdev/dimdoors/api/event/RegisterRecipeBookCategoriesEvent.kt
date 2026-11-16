package org.dimdev.dimdoors.api.event

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimcore.util.util.SimpleEvent
import java.util.function.Consumer

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
        @JvmField
        val EVENT: SimpleEvent<Consumer<RegisterRecipeBookCategoriesEvent>> = SimpleEvent.of { listeners -> { event ->
                listeners.forEach {
                    it.accept(event)
                }
            }
        }
    }
}