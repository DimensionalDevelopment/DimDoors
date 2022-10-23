package org.dimdev.dimdoors.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record TesselatingRecipe(Identifier id, String group, DefaultedList<Input> input, ItemStack output, float experience, int weavingTime) implements Recipe<Inventory> {
	@Override
	public boolean matches(Inventory inventory, World world) {
		List<Integer> usedIndices = new ArrayList<>();

		Predicate<ItemStack> predicate = stack -> {
			for (int i = 0; i < input().size(); i++) {
				if(!usedIndices.contains(i)) {
					Input input = input().get(i);

					if (input.test(stack)) {
						usedIndices.add(i);
						return true;
					}
				}
			}

			return false;
		};

		int i = 0;

		for(int j = 0; j < this.input.size(); ++j) {
			ItemStack itemStack = inventory.getStack(j);
			if (!itemStack.isEmpty() && predicate.test(itemStack)) {
				++i;
			}
		}

		return i == this.input.size();
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return this.output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height > this.input.size();
	}

	@Override
	public ItemStack getOutput() {
		return this.output();
	}

	@Override
	public Identifier getId() {
		return id();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeSerializers.TESSELATING;
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.TESSELATING;
	}

	public static class Serializer implements RecipeSerializer<TesselatingRecipe> {

		@Override
		public TesselatingRecipe read(Identifier id, JsonObject json) {
			String group = JsonHelper.getString(json, "group", "");
			DefaultedList<Input> defaultedList = getIngredients(JsonHelper.getArray(json, "ingredients"));
			if (defaultedList.isEmpty()) {
				throw new JsonParseException("No ingredients for tesselating recipe");
			} else if (defaultedList.size() > 3) {
				throw new JsonParseException("Too many ingredients for tesselating recipe");
			} else {
				JsonObject result = JsonHelper.getObject(json, "result");
				Identifier resultIdentifier = new Identifier(result.getAsJsonPrimitive("item").getAsString());
				ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(resultIdentifier).orElseThrow(() -> new IllegalStateException("Item: " + resultIdentifier + " does not exist")));
				float experience = JsonHelper.getFloat(json, "experience", 0.0F);
				int weavingTime = JsonHelper.getInt(json, "weavingtime", 200);

				return new TesselatingRecipe(id, group, defaultedList, itemStack, experience, weavingTime);
			}
		}


		private static DefaultedList<Input> getIngredients(JsonArray json) {
			DefaultedList<Input> defaultedList = DefaultedList.of();

			for(int i = 0; i < json.size(); ++i) {
				Ingredient ingredient = Ingredient.fromJson(json.get(i).getAsJsonObject().getAsJsonObject("ingredient"));
				if (!ingredient.isEmpty()) {
					defaultedList.add(new Input(ingredient, json.get(i).getAsJsonObject().getAsJsonPrimitive("count").getAsInt()));
				}
			}

			return defaultedList;
		}

		@Override
		public TesselatingRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString();

			DefaultedList<Input> ingredient = DefaultedList.ofSize(buf.readVarInt(), Input.EMPTY);

			for (int i = 0; i < ingredient.size(); i++) {
				ingredient.set(i, new Input(Ingredient.fromPacket(buf), buf.readInt()));
			}

			ItemStack itemStack = buf.readItemStack();

			float experience = buf.readFloat();

			int weavingTime = buf.readVarInt();

			return new TesselatingRecipe(id, group, ingredient, itemStack, experience, weavingTime);
		}

		@Override
		public void write(PacketByteBuf buf, TesselatingRecipe recipe) {
			buf.writeString(recipe.group());
			buf.writeVarInt(recipe.getIngredients().size());
			recipe.input().forEach(ingredient -> {
				ingredient.getLeft().write(buf);
				buf.writeInt(ingredient.getRight());
			});
			buf.writeFloat(recipe.experience());
			buf.writeVarInt(recipe.weavingTime());
		}
	}

	@Override
	public boolean isEmpty() {
		DefaultedList<Input> defaultedList = this.input();
		return defaultedList.isEmpty() || defaultedList.stream().anyMatch(Input::isEmpty);
	}

	public static class Input extends Pair<Ingredient, Integer> implements Predicate<ItemStack> {
		public static final Input EMPTY = new Input(null, 0);

		public Input(Ingredient left, Integer right) {
			super(left, right);
		}

		@Override
		public boolean test(ItemStack itemStack) {
			return itemStack.getCount() >= getRight() && getLeft().test(itemStack);
		}

		public boolean isEmpty() {
			return getRight() == 0;
		}
	}
}
