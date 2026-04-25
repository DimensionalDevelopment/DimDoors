package org.dimdev.dimdoors.client.neoforge;

import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ModRecipeBookTypesImpl {
	public static final EnumProxy<RecipeBookType> TESSELLATING = new EnumProxy<>(RecipeBookType.class);

	public static RecipeBookType getRecipeBookType(String name) {
		return TESSELLATING.getValue();
	}
}
