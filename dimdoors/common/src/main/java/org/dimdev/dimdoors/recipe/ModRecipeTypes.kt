package org.dimdev.dimdoors.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModRecipeTypes {
    @JvmField
    var TESSELATING: RecipeType<TesselatingRecipe> = register<TesselatingRecipe?>("tesselating")


    private fun <T : TesselatingRecipe?> register(name: String?): RecipeType<T?> {
        val id = DimensionalDoors.id(name!!)
        return getSided().register(Registries.RECIPE_TYPE, id, object : RecipeType<T?> {
            override fun toString(): String {
                return id.toString()
            }
        })
    }

    fun init() {
    }
}
