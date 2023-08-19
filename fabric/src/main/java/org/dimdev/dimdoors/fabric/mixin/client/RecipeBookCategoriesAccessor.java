package org.dimdev.dimdoors.fabric.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(RecipeBookCategories.class)
public interface RecipeBookCategoriesAccessor {
	@Accessor("AGGREGATE_CATEGORIES")
	public static Map<RecipeBookCategories, List<RecipeBookCategories>> aggregateCategories() {
		throw new AssertionError("Untransformed @Accessor");
	}

	@Accessor("AGGREGATE_CATEGORIES")
	@Mutable
	public static void setAggregateCategories(Map<RecipeBookCategories, List<RecipeBookCategories>> aggregateCategories) {
		throw new AssertionError("Untransformed @Accessor");
	}
}
