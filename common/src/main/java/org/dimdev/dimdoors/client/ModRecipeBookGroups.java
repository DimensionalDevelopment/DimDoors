package org.dimdev.dimdoors.client;

import com.google.common.base.Suppliers;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;

import java.util.List;
import java.util.function.Supplier;

public final class ModRecipeBookGroups {
	public static final Supplier<RecipeBookCategories> TESSELATING_GENERAL = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_GENERAL", () -> ModItems.WORLD_THREAD.get().getDefaultInstance()).get());
	public static final Supplier<RecipeBookCategories> TESSELATING_SEARCH = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_SEARCH", Items.COMPASS::getDefaultInstance).get());

	private static boolean initialized;

	private ModRecipeBookGroups() {
	}

	@ExpectPlatform
	private static Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
		throw new AssertionError();
	}

	public static void init() {
		if (initialized) {
			return;
		}

		initialized = true;
		RegisterRecipeBookCategoriesEvent.EVENT.register(event -> {
			event.registerBookCategories(ModRecipeBookTypes.TESSELLATING, List.of(TESSELATING_GENERAL.get(), TESSELATING_SEARCH.get()));
			event.registerAggregateCategory(TESSELATING_SEARCH.get(), List.of(TESSELATING_GENERAL.get()));
			event.registerRecipeCategoryFinder(ModRecipeTypes.TESSELATING.get(), recipeHolder -> TESSELATING_GENERAL.get());
		});
	}
}
