package org.dimdev.dimdoors.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.Utils;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static RecipeSerializer<ShapedTesselatingRecipe> SHAPED_TESSELATING = register("shaped_tesselating", new ShapedTesselatingRecipe.Serializer());
    public static RecipeSerializer<TesselatingShapelessRecipe> SHAPELESS_TESSELATING = register("shapeless_tesselating", new TesselatingShapelessRecipe.Serializer());

    public static void init() {
    }

    public static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> supplier) {
        return DimensionalDoors.getSided().register(Registries.RECIPE_SERIALIZER, name, supplier);
    }
}
