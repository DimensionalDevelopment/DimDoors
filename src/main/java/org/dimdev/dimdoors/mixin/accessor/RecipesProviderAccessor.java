package org.dimdev.dimdoors.mixin.accessor;

import java.nio.file.Path;

import com.google.gson.JsonObject;
import net.minecraft.data.server.RecipeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.data.DataCache;

@Mixin(RecipeProvider.class)
public interface RecipesProviderAccessor {
	@Invoker
	static void callSaveRecipe(DataCache cache, JsonObject json, Path path) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static void callSaveRecipeAdvancement(DataCache cache, JsonObject json, Path path) {
		throw new UnsupportedOperationException();
	}
}
