package org.dimdev.dimdoors.fabric.mixin.client;

import net.minecraft.client.ClientRecipeBook;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientRecipeBook.class)
public class RecipeBookManagerMixin {
//    @Inject(method = "getCategory(Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/client/RecipeBookCategories;", at = @At("HEAD"), cancellable = true)
//    private static void getCategory(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
//        var category = RecipeBookManager.findCategories(recipe.getType(), recipe);
//
//        if(category != null) cir.setReturnValue(category);
//    }
}
