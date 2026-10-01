package org.dimdev.dimdoors.recipe

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModRecipeTypes : PlatformRegistry<RecipeType<*>>(Registries.RECIPE_TYPE, BuiltInRegistries.RECIPE_TYPE, getSided()) {
    @JvmField
    var TESSELATING = register<TesselatingRecipe>("tesselating")


    private fun <T : TesselatingRecipe?> register(name: String): RecipeType<T> {
        val id = DimensionalDoors.id(name)
        return create(name) {
            object : RecipeType<T> {
                override fun toString(): String {
                    return id.toString()
                }
            }
        }
    }
}
