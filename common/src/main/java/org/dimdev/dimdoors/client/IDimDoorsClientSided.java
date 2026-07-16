package org.dimdev.dimdoors.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.limlib.api.client.IClientSided;

import java.util.function.Supplier;

public interface IDimDoorsClientSided<T extends IDimDoorsClientSided<T>> extends IClientSided<T> {
    default void checkCompat() {}

    Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack);

    VoidDimensionSpecialEffects createVoidEffect(DimensionEffect effect);
}
