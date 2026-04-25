package org.dimdev.dimdoors.client.neoforge;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import org.dimdev.dimdoors.item.ModItems;

import java.util.List;
import java.util.function.Supplier;

public class ModRecipeBookGroupsImpl {
	public static final EnumProxy<RecipeBookCategories> TESSELLATING_GENERAL = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModItems.WORLD_THREAD.get().getDefaultInstance()));
	public static final EnumProxy<RecipeBookCategories> TESSELLATING_SEARCH = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(Items.COMPASS.getDefaultInstance()));

	public static Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
		return switch (name) {
			case "TESSELATING_GENERAL" -> TESSELLATING_GENERAL::getValue;
			case "TESSELATING_SEARCH" -> TESSELLATING_SEARCH::getValue;
			default -> throw new IllegalArgumentException("Unknown tesselating recipe book category: " + name);
		};
	}
}
