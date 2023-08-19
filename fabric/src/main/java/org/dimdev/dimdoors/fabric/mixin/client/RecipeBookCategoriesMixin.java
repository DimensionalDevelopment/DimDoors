package org.dimdev.dimdoors.fabric.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import org.dimdev.dimdoors.client.ModRecipeBookGroups;
import org.dimdev.dimdoors.client.ModRecipeBookTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin {

    @Inject(method = "getCategories(Lnet/minecraft/world/inventory/RecipeBookType;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void getCategories(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> cir) {
        if(recipeBookType == ModRecipeBookTypes.TESSELLATING) cir.setReturnValue(ModRecipeBookGroups.TESSELATING_CATEGORIES.get());
    }
}
