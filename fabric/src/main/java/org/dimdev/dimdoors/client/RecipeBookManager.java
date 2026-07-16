package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent;
import org.dimdev.dimdoors.mixin.client.RecipeBookCategoriesAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class RecipeBookManager {
    private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_CATEGORIES = new HashMap<>();
    private static final Map<RecipeType<?>, Function<RecipeHolder<?>, RecipeBookCategories>> RECIPE_CATEGORY_LOOKUPS = new HashMap<>();
    private static boolean initialized;

    private RecipeBookManager() {
    }

    @Nullable
    public static <T extends Recipe<?>> RecipeBookCategories findCategories(RecipeType<T> type, RecipeHolder<T> recipe) {
        var lookup = RECIPE_CATEGORY_LOOKUPS.get(type);
        return lookup != null ? lookup.apply(recipe) : null;
    }

    public static List<RecipeBookCategories> getCustomCategoriesOrEmpty(RecipeBookType recipeBookType) {
        return TYPE_CATEGORIES.getOrDefault(recipeBookType, List.of());
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ModRecipeBookGroups.init();

        var typeCategories = new HashMap<RecipeBookType, List<RecipeBookCategories>>();
        var recipeCategoryLookups = new HashMap<RecipeType<?>, Function<RecipeHolder<?>, RecipeBookCategories>>();
        var event = new RegisterRecipeBookCategoriesEvent(getRegistry()::put, typeCategories::put, recipeCategoryLookups::put);
        RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(event);
        TYPE_CATEGORIES.putAll(typeCategories);
        RECIPE_CATEGORY_LOOKUPS.putAll(recipeCategoryLookups);
    }

    private static Map<RecipeBookCategories, List<RecipeBookCategories>> getRegistry() {
        return ImmutableCollectionUtils.getAsMutableMap(RecipeBookCategoriesAccessor::aggregateCategories, RecipeBookCategoriesAccessor::setAggregateCategories);
    }
}
