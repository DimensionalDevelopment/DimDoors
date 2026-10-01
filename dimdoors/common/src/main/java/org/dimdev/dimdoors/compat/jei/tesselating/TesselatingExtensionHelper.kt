package org.dimdev.dimdoors.compat.jei.tesselating

import mezz.jei.library.util.RecipeErrorUtil
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import java.util.*

class TesselatingExtensionHelper {
    private val handlers = mutableListOf<Handler<out TesselatingRecipe>>()
    private val handledClasses = mutableSetOf<Class<out TesselatingRecipe>>()
    private val cache = IdentityHashMap<RecipeHolder<out TesselatingRecipe>, ITesselatingCategoryExtension<out TesselatingRecipe>?>()

    fun <T : TesselatingRecipe> addRecipeExtension(
        recipeClass: Class<out T>,
        recipeExtension: ITesselatingCategoryExtension<T>
    ) {
        require(TesselatingRecipe::class.java.isAssignableFrom(recipeClass)) { "Recipe handlers must handle a specific class that inherits from CraftingRecipe. Instead got: $recipeClass" }
        require(!this.handledClasses.contains(recipeClass)) { "A Recipe Extension has already been registered for this class:$recipeClass" }
        this.handledClasses.add(recipeClass)
        this.handlers.add(Handler(recipeClass, recipeExtension))
    }

    fun <R : TesselatingRecipe> getRecipeExtension(recipeHolder: RecipeHolder<R>): ITesselatingCategoryExtension<R> {
        return getOptionalRecipeExtension(recipeHolder) ?: throw RuntimeException("Failed to create recipe extension for recipe: ${RecipeErrorUtil.getNameForRecipe(recipeHolder)}")
    }

    fun <R : TesselatingRecipe> getOptionalRecipeExtension(recipeHolder: RecipeHolder<R>): ITesselatingCategoryExtension<R>? {
        if (cache.containsKey(recipeHolder)) {
            return cache[recipeHolder]?.castOrNull<ITesselatingCategoryExtension<R>>()
        }

        val result = getBestRecipeHandler(recipeHolder)?.extension

        cache[recipeHolder] = result

        return result
    }

    private fun <T : TesselatingRecipe> getRecipeHandlerStream(recipeHolder: RecipeHolder<T>): Sequence<Handler<T>> {
        return handlers.mapNotNull { handler -> handler.optionalCast(recipeHolder) }.asSequence()
    }

    private fun <T : TesselatingRecipe> getBestRecipeHandler(recipeHolder: RecipeHolder<T>): Handler<T>? {
        val recipeClass: Class<out TesselatingRecipe> = recipeHolder.value().javaClass

        val assignableHandlers = mutableListOf<Handler<T>>()
        // try to find an exact match
        val allHandlers = getRecipeHandlerStream<T>(recipeHolder).toList()
        for (handler in allHandlers) {
            val handlerRecipeClass: Class<out TesselatingRecipe> = handler.recipeClass
            if (handlerRecipeClass == recipeClass) {
                return handler
            }
            // remove any handlers that are super of this one
            assignableHandlers.removeIf { h -> h.recipeClass.isAssignableFrom(handlerRecipeClass) }
            // only add this if it's not a super class of another assignable handler
            if (assignableHandlers.none { h -> handlerRecipeClass.isAssignableFrom(h.recipeClass) })
                assignableHandlers.add(handler)
        }
        if (assignableHandlers.isEmpty()) {
            return null
        }
        if (assignableHandlers.size == 1) {
            return assignableHandlers.first()
        }

        // try super classes to get the closest match
        var superClass: Class<*> = recipeClass
        while (Any::class.java != superClass) {
            superClass = superClass.superclass ?: break
            for (handler in assignableHandlers) {
                if (handler.recipeClass == superClass) {
                    return handler
                }
            }
        }

        val assignableClasses = assignableHandlers.map { it.recipeClass }
        LOGGER.warn("Found multiple matching recipe handlers for {}: {}", recipeClass, assignableClasses)
        return assignableHandlers.first()
    }

    private data class Handler<T : TesselatingRecipe>(
        val recipeClass: Class<out T>,
        val extension: ITesselatingCategoryExtension<T>
    ) {
        fun <V : TesselatingRecipe> optionalCast(recipeHolder: RecipeHolder<V>): Handler<V>? {
            if (isHandled(recipeHolder)) {
                val cast = this as Handler<V>
                return cast
            }
            return null
        }

        fun isHandled(recipeHolder: RecipeHolder<*>): Boolean {
            val recipe: Recipe<*> = recipeHolder.value()
            if (recipeClass.isInstance(recipe)) {
                val cast = recipeHolder as RecipeHolder<T>
                return extension.isHandled(cast)
            }
            return false
        }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
    }
}