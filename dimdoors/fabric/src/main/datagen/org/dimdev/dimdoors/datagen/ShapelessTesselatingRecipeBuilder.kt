package org.dimdev.dimdoors.datagen

import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe

class ShapelessTesselatingRecipeBuilder(result: ItemStack) :
    SimpleTesselatingRecipeBuilder<TesselatingShapelessRecipe, NonNullList<Ingredient>>(result) {
    private val ingredients = NonNullList.create<Ingredient>()

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    fun requires(tag: TagKey<Item>): ShapelessTesselatingRecipeBuilder {
        return this.requires(Ingredient.of(tag))
    }

    /**
     * Adds the given ingredient multiple times.
     */
    /**
     * Adds an ingredient of the given item.
     */
    fun requires(item: ItemLike, quantity: Int = 1): ShapelessTesselatingRecipeBuilder {
        for (i in 0..<quantity) {
            this.requires(Ingredient.of(item))
        }
        return this
    }

    /**
     * Adds an ingredient multiple times.
     */
    /**
     * Adds an ingredient.
     */
    @JvmOverloads
    fun requires(ingredient: Ingredient, quantity: Int = 1): ShapelessTesselatingRecipeBuilder {
        for (i in 0..<quantity) {
            this.ingredients.add(ingredient)
        }
        return this
    }

    public override fun ensureValid(id: ResourceLocation): NonNullList<Ingredient> {
        super.ensureValid(id)
        return ingredients
    }

    override fun createResult(stack: ItemStack, extraValue: NonNullList<Ingredient>): TesselatingShapelessRecipe {
        return TesselatingShapelessRecipe(this.group ?: "", stack, extraValue, weavingTime)
    }

    companion object {
        /**
         * Creates a new builder for a shapeless recipe.
         */
        /**
         * Creates a new builder for a shapeless recipe.
         */
        @JvmOverloads
        fun shapeless(result: ItemLike, count: Int = 1): ShapelessTesselatingRecipeBuilder {
            val stack = ItemStack(result, count)
            return ShapelessTesselatingRecipeBuilder(stack)
        }
    }
}


