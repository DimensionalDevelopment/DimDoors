package org.dimdev.dimdoors.datagen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.dimdev.dimdoors.mixin.accessor.RecipesProviderAccessor;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;

public class RecipeConsumer implements DataProvider, Consumer<RecipeJsonProvider> {
	private final DataGenerator dataGenerator;

	public RecipeConsumer(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	private final Set<RecipeJsonProvider> recipes = new HashSet<>();

	@Override
	public void run(DataCache cache) throws IOException {
		Path path = this.dataGenerator.getOutput();
		Set<Identifier> set = Sets.newHashSet();
		this.recipes.forEach((recipeJsonProvider) -> {
			if (!set.add(recipeJsonProvider.getRecipeId())) {
				throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
			} else {
				RecipesProviderAccessor.callSaveRecipe(cache, recipeJsonProvider.toJson(), path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json"));
				JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
				if (jsonObject != null) {
					RecipesProviderAccessor.callSaveRecipeAdvancement(cache, jsonObject, path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json"));
				}
			}
		});
	}

	@Override
	public String getName() {
		return "Recipe Provider";
	}

	@Override
	public void accept(RecipeJsonProvider recipeJsonProvider) {
		this.recipes.add(recipeJsonProvider);
	}
}
