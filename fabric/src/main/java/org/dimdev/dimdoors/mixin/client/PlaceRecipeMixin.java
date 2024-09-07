package org.dimdev.dimdoors.mixin.client;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

//@Mixin(PlaceRecipe.class)
//public interface PlaceRecipeMixin {
//    @Shadow void addItemToSlot(Iterator var1, int var2, int var3, int var4, int var5);
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    default public void placeRecipe(int width, int height, int outputSlot, Recipe<?> recipe, Iterator ingredients, int maxAmount) {
//        int i = width;
//        int j = height;
//        if (recipe instanceof ShapedRecipe shapedRecipe) {
//            i = shapedRecipe.getWidth();
//            j = shapedRecipe.getHeight();
//        } else if (recipe instanceof ShapedTesselatingRecipe shapedRecipe) {
//            i = shapedRecipe.getWidth();
//            j = shapedRecipe.getHeight();
//        }
//        int k = 0;
//        block0: for (int l = 0; l < height; ++l) {
//            if (k == outputSlot) {
//                ++k;
//            }
//            boolean bl = (float)j < (float)height / 2.0f;
//            int m = Mth.floor((float)height / 2.0f - (float)j / 2.0f);
//            if (bl && m > l) {
//                k += width;
//                ++l;
//            }
//            for (int n = 0; n < width; ++n) {
//                boolean bl2;
//                if (!ingredients.hasNext()) {
//                    return;
//                }
//                bl = (float)i < (float)width / 2.0f;
//                m = Mth.floor((float)width / 2.0f - (float)i / 2.0f);
//                int o = i;
//                boolean bl3 = bl2 = n < i;
//                if (bl) {
//                    o = m + i;
//                    boolean bl4 = bl2 = m <= n && n < m + i;
//                }
//                if (bl2) {
//                    addItemToSlot(ingredients, k, maxAmount, l, n);
//                } else if (o == n) {
//                    k += width - n;
//                    continue block0;
//                }
//                ++k;
//            }
//        }
//    }
//
//}
