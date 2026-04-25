package org.dimdev.dimdoors.api.util;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record RegisterRecipeBookCategoriesEvent(
    BiConsumer<RecipeBookCategories, List<RecipeBookCategories>> categoryAggregateCategory,
    BiConsumer<RecipeBookType, List<RecipeBookCategories>> bookCategories,
    BiConsumer<RecipeType<?>, Function<RecipeHolder<?>, RecipeBookCategories>> recipeCategoryFinder
) {
    public static final Event<Consumer<RegisterRecipeBookCategoriesEvent>> EVENT = EventFactory.createConsumerLoop();

    public void registerAggregateCategory(RecipeBookCategories category, List<RecipeBookCategories> other) {
    categoryAggregateCategory.accept(category, other);
    }

    public void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories) {
    bookCategories.accept(type, categories);
    }

    public void registerRecipeCategoryFinder(RecipeType<?> type, Function<RecipeHolder<?>, RecipeBookCategories> categoriesFunction) {
    recipeCategoryFinder.accept(type, categoriesFunction);
    }
}
