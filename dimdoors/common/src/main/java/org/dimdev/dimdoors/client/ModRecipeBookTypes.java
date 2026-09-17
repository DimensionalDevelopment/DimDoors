package org.dimdev.dimdoors.client;

import net.minecraft.world.inventory.RecipeBookType;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModRecipeBookTypes {
    public static final RecipeBookType TESSELLATING = DimensionalDoors.getSided().getTesselatingRecipeBookType();

    private ModRecipeBookTypes() {
    }

    public static void init() {
    }
}
