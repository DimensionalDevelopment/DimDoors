package org.dimdev.dimdoors.api.util;

import com.google.common.collect.ImmutableList;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record RegisterRecipeBookCategoriesEvent(BiConsumer<RecipeBookCategories, List<RecipeBookCategories>> categoryAggregateCategory, BiConsumer<RecipeBookType, List<RecipeBookCategories>> bookCategories, BiConsumer<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> recipeCategoryFinder) {
    public static final Event<Consumer<RegisterRecipeBookCategoriesEvent>> EVENT = EventFactory.createConsumerLoop();

    /**
     * Registers the list of categories that compose an aggregate category.
     */
    public void registerAggregateCategory(RecipeBookCategories category, List<RecipeBookCategories> other) {
        categoryAggregateCategory.accept(category, other);
    }
    /**
     * Registers the list of categories that compose a recipe book.
     */
    public void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories) {
        bookCategories.accept(type, categories);
    }

    /**
     * Registers a category lookup for a certain recipe type.
     */
    public void registerRecipeCategoryFinder(RecipeType<?> type, Function<Recipe<?>, RecipeBookCategories> categoriesFunction) {
        recipeCategoryFinder.accept(type, categoriesFunction);
    }


}
