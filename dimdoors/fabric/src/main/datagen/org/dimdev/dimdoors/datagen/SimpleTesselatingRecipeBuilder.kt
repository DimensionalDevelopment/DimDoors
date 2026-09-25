package org.dimdev.dimdoors.datagen

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.recipe.TesselatingRecipe

abstract class SimpleTesselatingRecipeBuilder<T : TesselatingRecipe, V>(private val result: ItemStack) : TesselatingRecipeBuilder<T, V>() {
    override fun getResult(): Item = this.result.getItem()

    override fun createResult(extraValue: V): T {
        return createResult(result, extraValue)
    }

    protected abstract fun createResult(stack: ItemStack, extraValue: V): T
}