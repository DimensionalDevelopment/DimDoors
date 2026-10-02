package org.dimdev.dimdoors.client

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent
import org.dimdev.dimdoors.client.DimensionalDoorsClient.Companion.clientSided
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import java.util.function.Consumer

object ModRecipeBookGroups {
    val TESSELATING_GENERAL: RecipeBookCategories by lazy { getRecipBookCategories("TESSELATING_GENERAL") { ModItems.WORLD_THREAD.defaultInstance } }
    val TESSELATING_SEARCH: RecipeBookCategories by lazy { getRecipBookCategories("TESSELATING_SEARCH") { Items.COMPASS.defaultInstance } }

    private var initialized = false

    private fun getRecipBookCategories(name: String, itemStack: () -> ItemStack): RecipeBookCategories = clientSided.getRecipBookCategories(name, itemStack)()

    fun init() {
        if (initialized) {
            return
        }

        initialized = true
        RegisterRecipeBookCategoriesEvent.EVENT.register(Consumer { event ->
            event.registerBookCategories(getSided().tesselatingRecipeBookType, mutableListOf(TESSELATING_GENERAL, TESSELATING_SEARCH))
            event.registerAggregateCategory(TESSELATING_SEARCH, mutableListOf(TESSELATING_GENERAL))
            event.registerRecipeCategoryFinder(ModRecipeTypes.TESSELATING) { TESSELATING_GENERAL }
        })
    }
}
