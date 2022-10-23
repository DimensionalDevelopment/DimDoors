package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class TesselatingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final Item output;
	private final int outputCount;
	private final List<Pair<Integer, Ingredient>> inputs = Lists.newArrayList();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
	private final float experience;
	private final int weavingTime;

	@Nullable
	private String group;

	public TesselatingRecipeJsonBuilder(ItemConvertible output, int outputCount, float experience, int weavingTime) {
		this.output = output.asItem();
		this.outputCount = outputCount;
		this.experience = experience;
		this.weavingTime = weavingTime;
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output, int outputCount, float experience, int weavingTime) {
		return new TesselatingRecipeJsonBuilder(output, outputCount, experience, weavingTime);
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output, int outputCount, float experience) {
		return create(output, outputCount, experience, 100);
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output, int outputCount) {
		return create(output, outputCount, 0.2f);
	}

	public static TesselatingRecipeJsonBuilder create(ItemConvertible output) {
		return create(output, 1);
	}

	public TesselatingRecipeJsonBuilder input(TagKey<Item> tag) {
		return this.input(Ingredient.fromTag(tag));
	}

	public TesselatingRecipeJsonBuilder input(ItemConvertible itemProvider) {
		return this.input((ItemConvertible)itemProvider, 1);
	}

	public TesselatingRecipeJsonBuilder input(ItemConvertible itemProvider, int size) {
		this.input(Ingredient.ofItems(itemProvider), size);

		return this;
	}

	public TesselatingRecipeJsonBuilder input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public TesselatingRecipeJsonBuilder input(Ingredient ingredient, int size) {
		this.inputs.add(Pair.of(size, ingredient));
		return this;
	}

	public TesselatingRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
		this.advancementBuilder.criterion(string, criterionConditions);
		return this;
	}

	public TesselatingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public Item getOutputItem() {
		return this.output;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", (CriterionConditions) RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
		String var10006 = this.group == null ? "" : this.group;
		String var10011 = recipeId.getNamespace();
		String var10012 = this.output.getGroup().getName();
		exporter.accept(new TesselatingRecipeJsonProvider(recipeId, this.output, this.outputCount, experience, weavingTime, var10006, this.inputs, this.advancementBuilder, new Identifier(var10011, "recipes/" + var10012 + "/" + recipeId.getPath())));
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class TesselatingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int count;
		private final String group;
		private final float experience;
		private final int weavingTime;
		private final List<Pair<Integer, Ingredient>> inputs;
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;

		public TesselatingRecipeJsonProvider(Identifier recipeId, Item output, int outputCount, float experience, int weavingTime, String group, List<Pair<Integer, Ingredient>> inputs, Advancement.Builder advancementBuilder, Identifier advancementId) {
			this.recipeId = recipeId;
			this.output = output;
			this.count = outputCount;
			this.experience = experience;
			this.weavingTime = weavingTime;
			this.group = group;
			this.inputs = inputs;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for (Pair<Integer, Ingredient> ingredient : this.inputs) {
				JsonObject element = new JsonObject();
				element.add("ingredient", ingredient.getSecond().toJson());
				element.addProperty("count", ingredient.getFirst());
				jsonArray.add(element);
			}

			json.add("ingredients", jsonArray);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.output).toString());
			if (this.count > 1) {
				jsonObject.addProperty("count", (Number)this.count);
			}

			json.add("result", jsonObject);
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
