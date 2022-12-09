package org.dimdev.dimdoors.mixin.accessor;

import com.google.gson.JsonObject;
import net.minecraft.data.DataWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(RecipeProvider.class)
public interface RecipesProviderAccessor {
	@Invoker
	static void callSaveRecipe(DataWriter cache, JsonObject json, Path path) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static void callSaveRecipeAdvancement(DataWriter cache, JsonObject json, Path path) {
		throw new UnsupportedOperationException();
	}
}
