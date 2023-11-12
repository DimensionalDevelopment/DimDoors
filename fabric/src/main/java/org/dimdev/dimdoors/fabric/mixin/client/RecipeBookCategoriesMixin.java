package org.dimdev.dimdoors.fabric.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin {

//    @Inject(method = "getCategories(Lnet/minecraft/world/inventory/RecipeBookType;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
//    private static void getCategories(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> cir) {
//        var categories = RecipeBookManager.getCustomCategoriesOrEmpty(recipeBookType);
//
//        if(!categories.isEmpty()) cir.setReturnValue(categories);
//    }
}
