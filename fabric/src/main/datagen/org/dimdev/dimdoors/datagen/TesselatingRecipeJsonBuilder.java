package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TesselatingRecipeJsonBuilder implements RecipeBuilder {
	private final Item result;
	private final int count;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();


	@Nullable
	private String group;

	private int weavingTime = 200;

	public TesselatingRecipeJsonBuilder(ItemLike result, int count) {
		this.result = result.asItem();
		this.count = count;
	}

	public static TesselatingRecipeJsonBuilder create(ItemLike output) {
		return create(output, 1);
	}

	public static TesselatingRecipeJsonBuilder create(ItemLike output, int outputCount) {
		return new TesselatingRecipeJsonBuilder(output, outputCount);
	}

	public TesselatingRecipeJsonBuilder define(Character c, TagKey<Item> tag) {
		return this.define(c, Ingredient.of(tag));
	}

	public TesselatingRecipeJsonBuilder define(Character c, ItemLike itemProvider) {
		return this.define(c, Ingredient.of(itemProvider));
	}

	public TesselatingRecipeJsonBuilder define(Character c, Ingredient ingredient) {
		if (this.key.containsKey(c)) {
			throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
		} else if (c == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(c, ingredient);
			return this;
		}
	}

	public TesselatingRecipeJsonBuilder pattern(String patternStr) {
		if (!this.rows.isEmpty() && patternStr.length() != this.rows.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.rows.add(patternStr);
			return this;
		}
	}

	@Override
	public TesselatingRecipeJsonBuilder unlockedBy(String name, Criterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public TesselatingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getResult() {
		return result;
	}

	public TesselatingRecipeJsonBuilder weavingTime(int weavingTime) {
		this.weavingTime = weavingTime;
		return this;
	}

	@Override
	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		ShapedRecipePattern shapedRecipePattern = this.ensureValid(id);
		Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(builder::addCriterion);
		ShapedTesselatingRecipe shapedRecipe = new ShapedTesselatingRecipe(Objects.requireNonNullElse(this.group, ""), shapedRecipePattern, new ItemStack(this.result, this.count), weavingTime);
		recipeOutput.accept(id, shapedRecipe, builder.build(id.withPrefix("recipes/tesselating/")));
	}

	private ShapedRecipePattern ensureValid(ResourceLocation loaction) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(loaction));
		} else {
			return ShapedRecipePattern.of(this.key, this.rows);
		}
	}
}
