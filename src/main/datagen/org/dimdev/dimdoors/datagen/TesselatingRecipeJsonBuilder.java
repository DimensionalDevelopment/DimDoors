package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TesselatingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final int outputCount;
	private final List<String> pattern = Lists.newArrayList();
	private final Map<Character, Ingredient> inputs = Maps.newLinkedHashMap();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.create();

	@Nullable
	private String group;

	private float experience = 1.0f;
	private int weavingTime = 200;

	public TesselatingRecipeJsonBuilder(ItemConvertible output, int outputCount) {
		this.category = RecipeCategory.MISC; //TODO: FIND out if need to be different
		this.output = output.asItem();
		this.outputCount = outputCount;
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output) {
		return create(output, 1);
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output, int outputCount) {
		return new TesselatingRecipeJsonBuilder(output, outputCount);
	}

	public TesselatingRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
		return this.input(c, Ingredient.fromTag(tag));
	}

	public TesselatingRecipeJsonBuilder input(Character c, ItemConvertible itemProvider) {
		return this.input(c, Ingredient.ofItems(itemProvider));
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

	public TesselatingRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
		this.advancementBuilder.criterion(string, criterionConditions);
		return this;
	}

	public TesselatingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public TesselatingRecipeJsonBuilder experience(float experience) {
		this.experience = experience;
		return this;
	}

	public TesselatingRecipeJsonBuilder weavingTime(int weavingTime) {
		this.weavingTime = weavingTime;
		return this;
	}

	public Item getOutputItem() {
		return this.output;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);

		exporter.accept(new TesselatingRecipeJsonProvider(recipeId, this.output, this.outputCount, this.group == null ? "" : this.group, this.pattern, this.inputs, this.advancementBuilder, recipeId.withPrefixedPath("recipes/tesselating/"), experience, weavingTime));
	}

	private void validate(Identifier recipeId) {
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

	static class TesselatingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int resultCount;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> inputs;
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;
		private final float experience;
		private final int weavingTime;

		public TesselatingRecipeJsonProvider(Identifier recipeId, Item output, int resultCount, String group, List<String> pattern, Map<Character, Ingredient> inputs, Advancement.Builder advancementBuilder, Identifier advancementId, float experience, int weavingTime) {
			this.recipeId = recipeId;
			this.output = output;
			this.resultCount = resultCount;
			this.group = group;
			this.pattern = pattern;
			this.inputs = inputs;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
			this.experience = experience;
			this.weavingTime = weavingTime;
		}

		public void serialize(JsonObject json) {
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
			jsonObject2.addProperty("item", Registries.ITEM.getId(this.output).toString());
			if (this.resultCount > 1) {
				jsonObject2.addProperty("count", this.resultCount);
			}

			json.add("result", jsonObject2);
			json.addProperty("experience", experience);
			json.addProperty("weavingtime", weavingTime);
		}

		public RecipeSerializer<?> getSerializer() {
			return ModRecipeSerializers.TESSELATING;
		}

		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Nullable
		public JsonObject toAdvancementJson() {
			return this.advancementBuilder.toJson();
		}

		@Nullable
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
