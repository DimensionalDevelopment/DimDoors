package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;

import java.util.List;
import java.util.Map;

public class ShapedTesselatingRecipeJsonBuilder extends SimpleTesselatingRecipeBuilder<ShapedTesselatingRecipe, ShapedRecipePattern> {
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final boolean showNotification = true;

	public ShapedTesselatingRecipeJsonBuilder(ItemStack result) {
		super(result);
	}

	public static ShapedTesselatingRecipeJsonBuilder shaped(ItemLike output) {
		return shaped(output, 1);
	}

	public static ShapedTesselatingRecipeJsonBuilder shaped(ItemLike output, int outputCount) {
		var stack = new ItemStack(output, outputCount);
		return new ShapedTesselatingRecipeJsonBuilder(stack);
	}

	public ShapedTesselatingRecipeJsonBuilder define(Character c, TagKey<Item> tag) {
		return this.define(c, Ingredient.of(tag));
	}

	public ShapedTesselatingRecipeJsonBuilder define(Character c, ItemLike itemProvider) {
		return this.define(c, Ingredient.of(itemProvider));
	}

	public ShapedTesselatingRecipeJsonBuilder define(Character symbol, Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(symbol, ingredient);
			return this;
		}
	}

	public ShapedTesselatingRecipeJsonBuilder pattern(String patternStr) {
		if (!this.rows.isEmpty() && patternStr.length() != this.rows.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.rows.add(patternStr);
			return this;
		}
	}

	@Override
	protected ShapedTesselatingRecipe createResult(ItemStack result, ShapedRecipePattern pattern) {
		return new ShapedTesselatingRecipe(this.group == null ? "" : this.group, pattern, result, weavingTime, this.showNotification);
	}

	public ShapedRecipePattern ensureValid(ResourceLocation resourceLocation) {
		super.ensureValid(resourceLocation);
		return ShapedRecipePattern.of(this.key, this.rows);
	}
}