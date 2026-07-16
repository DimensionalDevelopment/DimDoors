package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.client.RecipeBookManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class RecipeBookManagerMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "getCategory(Lnet/minecraft/world/item/crafting/RecipeHolder;)Lnet/minecraft/client/RecipeBookCategories;", at = @At("HEAD"), cancellable = true)
    private static void getCategory(RecipeHolder<?> recipeHolder, CallbackInfoReturnable<RecipeBookCategories> cir) {
        var category = RecipeBookManager.findCategories((RecipeType) recipeHolder.value().getType(), (RecipeHolder) recipeHolder);
        if (category != null) {
            cir.setReturnValue(category);
        }
    }
}
