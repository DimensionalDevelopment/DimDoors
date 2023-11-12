package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TesselatingRecipeJsonBuilder extends CraftingRecipeBuilder {
	private final Item output;
	private final int outputCount;
	private final List<String> pattern = Lists.newArrayList();
	private final Map<Character, Ingredient> inputs = Maps.newLinkedHashMap();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

	@Nullable
	private String group;

	private int weavingTime = 200;

	public TesselatingRecipeJsonBuilder(ItemLike output, int outputCount) {
		this.output = output.asItem();
		this.outputCount = outputCount;
	}

	public static TesselatingRecipeJsonBuilder create(ItemLike output) {
		return create(output, 1);
	}

	public static TesselatingRecipeJsonBuilder create(ItemLike output, int outputCount) {
		return new TesselatingRecipeJsonBuilder(output, outputCount);
	}

	public TesselatingRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
		return this.input(c, Ingredient.of(tag));
	}

	public TesselatingRecipeJsonBuilder input(Character c, ItemLike itemProvider) {
		return this.input(c, Ingredient.of(itemProvider));
	}

	public TesselatingRecipeJsonBuilder input(Character c, Ingredient ingredient) {
		if (this.inputs.containsKey(c)) {
			throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
		} else if (c == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.inputs.put(c, ingredient);
			return this;
		}
	}

	public TesselatingRecipeJsonBuilder pattern(String patternStr) {
		if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.pattern.add(patternStr);
			return this;
		}
	}

	public TesselatingRecipeJsonBuilder criterion(String string, CriterionTriggerInstance criterionConditions) {
		this.advancementBuilder.addCriterion(string, criterionConditions);
		return this;
	}

	public TesselatingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public TesselatingRecipeJsonBuilder weavingTime(int weavingTime) {
		this.weavingTime = weavingTime;
		return this;
	}

	public Item getOutputItem() {
		return this.output;
	}

	public void offerTo(Consumer<FinishedRecipe> exporter, ResourceLocation recipeId) {
		recipeId = recipeId.withPrefix("tesselating/");
		this.validate(recipeId);
		this.advancementBuilder.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);

		exporter.accept(new TesselatingRecipeJsonProvider(recipeId, this.output, this.outputCount, this.group == null ? "" : this.group, this.pattern, this.inputs, this.advancementBuilder, recipeId.withPrefix("recipes/tesselating/"), weavingTime));
	}

	private void validate(ResourceLocation recipeId) {
		if (this.pattern.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
		} else {
			Set<Character> set = Sets.newHashSet(this.inputs.keySet());
			set.remove(' ');

			for (String string : this.pattern) {
				for (int i = 0; i < string.length(); ++i) {
					char c = string.charAt(i);
					if (!this.inputs.containsKey(c) && c != ' ') {
						throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + c + "'");
					}

					set.remove(c);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
			} else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.advancementBuilder.getCriteria().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + recipeId);
			}
		}
	}

	static class TesselatingRecipeJsonProvider implements FinishedRecipe {
		private final ResourceLocation recipeId;
		private final Item output;
		private final int resultCount;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> inputs;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;
		private final int weavingTime;

		public TesselatingRecipeJsonProvider(ResourceLocation recipeId, Item output, int resultCount, String group, List<String> pattern, Map<Character, Ingredient> inputs, Advancement.Builder advancementBuilder, ResourceLocation advancementId, int weavingTime) {
			this.recipeId = recipeId;
			this.output = output;
			this.resultCount = resultCount;
			this.group = group;
			this.pattern = pattern;
			this.inputs = inputs;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
			this.weavingTime = weavingTime;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for (String string : this.pattern) {
				jsonArray.add(string);
			}

			json.add("pattern", jsonArray);
			JsonObject jsonObject = new JsonObject();

			for (Map.Entry<Character, Ingredient> characterIngredientEntry : this.inputs.entrySet()) {
				jsonObject.add(String.valueOf(characterIngredientEntry.getKey()), characterIngredientEntry.getValue().toJson());
			}

			json.add("key", jsonObject);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("item", BuiltInRegistries.ITEM.getKey(this.output).toString());
			if (this.resultCount > 1) {
				jsonObject2.addProperty("count", this.resultCount);
			}

			json.add("result", jsonObject2);
			json.addProperty("weavingtime", weavingTime);
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ModRecipeSerializers.SHAPED_TESSELATING.get();
		}

		public ResourceLocation getId() {
			return this.recipeId;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return this.advancementBuilder.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}
}
