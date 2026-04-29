package org.dimdev.dimdoors.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.Utils;

public class ModRecipeTypes {
    public static RecipeType<TesselatingRecipe> TESSELATING = register("tesselating");


    private static <T extends TesselatingRecipe> RecipeType<T> register(String name) {
        var id = DimensionalDoors.id(name);
        return DimensionalDoors.getSided().register(Registries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    public static void init() {
    }
}
