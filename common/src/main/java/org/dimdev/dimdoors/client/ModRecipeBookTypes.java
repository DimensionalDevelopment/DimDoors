package org.dimdev.dimdoors.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.inventory.RecipeBookType;

public final class ModRecipeBookTypes {
	public static final RecipeBookType TESSELLATING = getRecipeBookType("TESSELLATING");

	private ModRecipeBookTypes() {
	}

	@ExpectPlatform
	private static RecipeBookType getRecipeBookType(String name) {
		throw new AssertionError();
	}

	public static void init() {
	}
}
