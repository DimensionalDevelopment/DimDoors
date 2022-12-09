<<<<<<< HEAD
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
=======
//TODO: Disabled unsure if needed.
//package org.dimdev.dimdoors.mixin.accessor;
//
//import com.google.gson.JsonObject;
//import net.minecraft.data.DataWriter;
//import net.minecraft.data.server.recipe.RecipeProvider;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.gen.Invoker;
//
//import java.nio.file.Path;
//
//@Mixin(RecipeProvider.class)
//public interface RecipesProviderAccessor {
//	@Invoker
//	static void callSaveRecipe(DataWriter cache, JsonObject json, Path path) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Invoker
//	static void callSaveRecipeAdvancement(DataWriter cache, JsonObject json, Path path) {
//		throw new UnsupportedOperationException();
//	}
//}
>>>>>>> 1094dcf08ea591e210aafa16d4b4c1141a2fae7b
