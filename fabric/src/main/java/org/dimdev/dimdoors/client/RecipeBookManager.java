package org.dimdev.dimdoors.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent;
import org.dimdev.dimdoors.fabric.mixin.client.RecipeBookCategoriesAccessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.RecipeBookCategories.*;

/**
 * Manager for {@link RecipeBookType recipe book types} and {@link RecipeBookCategories categories}.
 * <p>
 * Provides a recipe category lookup.
 */
public final class RecipeBookManager
{
    // Not using ConcurrentHashMap here because it's slower for lookups, so we only use it during init
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES = new HashMap<>();
    private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_CATEGORIES = new HashMap<>();
    private static final Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> RECIPE_CATEGORY_LOOKUPS = new HashMap<>();

    /**
     * Finds the category the specified recipe should display in, or null if none.
     */
    @Nullable
    public static RecipeBookCategories findCategories(RecipeType<?> type, Recipe<?> recipe)
    {
        var lookup = RECIPE_CATEGORY_LOOKUPS.get(type);
        return lookup != null ? lookup.apply(recipe) : null;
    }

    @ApiStatus.Internal
    public static List<RecipeBookCategories> getCustomCategoriesOrEmpty(RecipeBookType recipeBookType)
    {
        return TYPE_CATEGORIES.getOrDefault(recipeBookType, List.of());
    }

    @ApiStatus.Internal
    public static void init()
    {
        // The ImmutableMap is the patched out value of AGGREGATE_CATEGORIES
        var typeCategories = new HashMap<RecipeBookType, List<RecipeBookCategories>>();
        var recipeCategoryLookups = new HashMap<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>>();
        var event = new RegisterRecipeBookCategoriesEvent(getRegistry()::put, typeCategories::put, recipeCategoryLookups::put);
        RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(event);
        TYPE_CATEGORIES.putAll(typeCategories);
        RECIPE_CATEGORY_LOOKUPS.putAll(recipeCategoryLookups);
    }

    private static Map<RecipeBookCategories, List<RecipeBookCategories>> getRegistry() {
        return ImmutableCollectionUtils.getAsMutableMap(RecipeBookCategoriesAccessor::aggregateCategories, RecipeBookCategoriesAccessor::setAggregateCategories);
    }
}
