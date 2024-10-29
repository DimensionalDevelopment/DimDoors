package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShapedTesselatingRecipeJsonBuilder extends SimpleTesselatingRecipeBuilder {
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final boolean showNotification = true;

	public ShapedTesselatingRecipeJsonBuilder(ItemLike result, int count) {
		super(result, count);
	}

	public static ShapedTesselatingRecipeJsonBuilder shaped(ItemLike output) {
		return shaped(output, 1);
	}

	public static ShapedTesselatingRecipeJsonBuilder shaped(ItemLike output, int outputCount) {
		return new ShapedTesselatingRecipeJsonBuilder(output, outputCount);
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
	protected ShapedTesselatingRecipe createResult(ItemLike result, int count) {

		return new ShapedTesselatingRecipe(id, result, count, this.group == null ? "" : this.group, this.rows, this.key, builder.build(id), this.showNotification, weavingTime);
	}

	protected void ensureValid(ResourceLocation id) {
		super.ensureValid(id);
		if (this.rows.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
		} else {
			Set<Character> set = Sets.newHashSet(this.key.keySet());
			set.remove(' ');

			for (String row : this.rows) {
				for (int i = 0; i < row.length(); ++i) {
					char symbol = row.charAt(i);
					if (!this.key.containsKey(symbol) && symbol != ' ') {
						throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + symbol + "'");
					}

					set.remove(symbol);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
			} else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
			}
		}
	}

	protected static class Result extends SimpleResult {
		private final List<String> pattern;
		private final Map<Character, Ingredient> key;
		private final boolean showNotification;

        public Result(ResourceLocation id, ItemLike result, int count, String group, List<String> pattern, Map<Character, Ingredient> key, AdvancementHolder advancement, boolean showNotification, int weavingTime) {
			super(id, result, count, group, advancement, weavingTime);
			this.pattern = pattern;
			this.key = key;
			this.showNotification = showNotification;
        }

		public void serializeRecipeData(JsonObject json) {
			super.serializeRecipeData(json);
			JsonArray jsonArray = new JsonArray();

            for (String string : this.pattern) {
                jsonArray.add(string);
            }

			json.add("pattern", jsonArray);
			JsonObject jsonObject = new JsonObject();

            for (Map.Entry<Character, Ingredient> characterIngredientEntry : this.key.entrySet()) {
                jsonObject.add(String.valueOf(characterIngredientEntry.getKey()), characterIngredientEntry.getValue().toJson(false));
            }

			json.add("key", jsonObject);

			json.addProperty("show_notification", this.showNotification);
		}

		public RecipeSerializer<?> type() {
			return ModRecipeSerializers.SHAPED_TESSELATING.get();
		}
	}
}