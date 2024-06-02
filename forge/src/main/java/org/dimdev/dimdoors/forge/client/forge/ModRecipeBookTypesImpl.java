package org.dimdev.dimdoors.forge.client.forge;

import net.minecraft.world.inventory.RecipeBookType;

public class ModRecipeBookTypesImpl {
    public static RecipeBookType getRecipeBookType(String name) {
        return RecipeBookType.create(name);
    }
}
