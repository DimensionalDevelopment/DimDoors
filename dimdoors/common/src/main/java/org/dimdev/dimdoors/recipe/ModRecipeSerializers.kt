package org.dimdev.dimdoors.recipe

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModRecipeSerializers : PlatformRegistry<RecipeSerializer<*>>(Registries.RECIPE_SERIALIZER, BuiltInRegistries.RECIPE_SERIALIZER, getSided()) {
    var SHAPED_TESSELATING = create("shaped_tesselating") { ShapedTesselatingRecipe.Serializer() }
    var SHAPELESS_TESSELATING = create("shapeless_tesselating") { TesselatingShapelessRecipe.Serializer() }
}
