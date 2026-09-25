package org.dimdev.dimdoors.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimdoors.block.ModBlocks

interface TesselatingRecipe : Recipe<CraftingInput> {
    val weavingTime: Int

    override fun getType(): RecipeType<TesselatingRecipe> = ModRecipeTypes.TESSELATING

    override fun getToastSymbol(): ItemStack = ModBlocks.TESSELATING_LOOM.value().asItem().defaultInstance
}