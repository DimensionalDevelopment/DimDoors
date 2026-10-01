package org.dimdev.dimdoors.client

import net.minecraft.world.inventory.RecipeBookType
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModRecipeBookTypes {
    val TESSELLATING: RecipeBookType = getSided().tesselatingRecipeBookType

    fun register() {
    }
}
