package org.dimdev.dimdoors.client;

import com.google.common.base.Suppliers;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.List;
import java.util.function.Supplier;

public class ModRecipeBookGroups {
    public static final Supplier<RecipeBookCategories> TESSELATING_GENERAL = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_GENERAL", () -> ModItems.WORLD_THREAD.get().getDefaultInstance()).get());
    public static final Supplier<RecipeBookCategories> TESSELATING_SEARCH = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_SEARCH", Items.COMPASS::getDefaultInstance).get());

    public static final Supplier<List<RecipeBookCategories>> TESSELATING_CATEGORIES = Suppliers.memoize(() -> List.of(TESSELATING_GENERAL.get()));


    @ExpectPlatform
    private static Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        throw new RuntimeException();
    }

    public static void init() {
        System.out.println("Blarg: " + TESSELATING_GENERAL.get());
        System.out.println("Blarg: " + TESSELATING_SEARCH.get());
        System.out.println("Blarg: " + TESSELATING_CATEGORIES.get());

    }
}
