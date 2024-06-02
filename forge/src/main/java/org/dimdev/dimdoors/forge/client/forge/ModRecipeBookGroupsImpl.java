package org.dimdev.dimdoors.forge.client.forge;

import com.google.common.base.Suppliers;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModRecipeBookGroupsImpl {
    public static Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return () -> RecipeBookCategories.create(name, itemStack.get());
    }
}
