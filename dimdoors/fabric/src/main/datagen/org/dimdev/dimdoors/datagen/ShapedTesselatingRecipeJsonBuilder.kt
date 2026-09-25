package org.dimdev.dimdoors.datagen

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe

class ShapedTesselatingRecipeJsonBuilder(result: ItemStack) :
    SimpleTesselatingRecipeBuilder<ShapedTesselatingRecipe, ShapedRecipePattern>(result) {
    private val rows = mutableListOf<String>()
    private val key = mutableMapOf<Char, Ingredient>()
    private val showNotification = true

    fun define(c: Char, tag: TagKey<Item>): ShapedTesselatingRecipeJsonBuilder {
        return this.define(c, Ingredient.of(tag))
    }

    fun define(c: Char, itemProvider: Holder<out ItemLike>): ShapedTesselatingRecipeJsonBuilder {
        return define(c, itemProvider.value())
    }

    fun define(c: Char, itemProvider: ItemLike): ShapedTesselatingRecipeJsonBuilder {
        return this.define(c, Ingredient.of(itemProvider))
    }

    fun define(symbol: Char, ingredient: Ingredient): ShapedTesselatingRecipeJsonBuilder {
        require(!this.key.containsKey(symbol)) { "Symbol '$symbol' is already defined!" }
        require(symbol != ' ') { "Symbol ' ' (whitespace) is reserved and cannot be defined" }
        this.key[symbol] = ingredient
        return this
    }

    fun pattern(patternStr: String): ShapedTesselatingRecipeJsonBuilder {
        require(!(!this.rows.isEmpty() && patternStr.length != this.rows[0].length)) { "Pattern must be the same width on every line!" }
        this.rows.add(patternStr)
        return this
    }

    override fun createResult(result: ItemStack, pattern: ShapedRecipePattern) = ShapedTesselatingRecipe(
            this.group ?: "",
            pattern,
            result,
            weavingTime,
            this.showNotification
        )

    public override fun ensureValid(id: ResourceLocation): ShapedRecipePattern {
        super.ensureValid(id)
        return ShapedRecipePattern.of(this.key, this.rows)
    }

    companion object {
        @JvmOverloads
        fun shaped(output: ItemLike, outputCount: Int = 1): ShapedTesselatingRecipeJsonBuilder = ItemStack(output, outputCount).let(::ShapedTesselatingRecipeJsonBuilder)

        fun shaped(output: Holder<out ItemLike>) = shaped(output.value())
    }
}