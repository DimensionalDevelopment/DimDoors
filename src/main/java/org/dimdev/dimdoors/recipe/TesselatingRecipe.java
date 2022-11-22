package org.dimdev.dimdoors.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

public class TesselatingRecipe extends ShapedRecipe {
	public final float experience;
	public final int weavingTime;

	public TesselatingRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> defaultedList, ItemStack itemStack, float experience, int weavingTime) {
		super(id, group, width, height, defaultedList, itemStack);

		this.experience = experience;
		this.weavingTime = weavingTime;
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
		public TesselatingRecipe read(Identifier id, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Map<String, Ingredient> map = ShapedRecipe.readSymbols(JsonHelper.getObject(jsonObject, "key"));
			String[] strings = ShapedRecipe.removePadding(ShapedRecipe.getPattern(JsonHelper.getArray(jsonObject, "pattern")));
			int i = strings[0].length();
			int j = strings.length;
			DefaultedList<Ingredient> defaultedList = ShapedRecipe.createPatternMatrix(strings, map, i, j);
			ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			float experience = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
			int weavingTime = JsonHelper.getInt(jsonObject, "weavingtime", 200);

			return new TesselatingRecipe(id, string, i, j, defaultedList, itemStack, experience, weavingTime);
		}

		@Override
		public TesselatingRecipe read(Identifier id, PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			int j = packetByteBuf.readVarInt();
			String string = packetByteBuf.readString();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);

			for(int k = 0; k < defaultedList.size(); ++k) {
				defaultedList.set(k, Ingredient.fromPacket(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();

			float experience = packetByteBuf.readFloat();

			int weavingTime = packetByteBuf.readVarInt();

			return new TesselatingRecipe(id, string, i, j, defaultedList, itemStack, experience, weavingTime);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf, TesselatingRecipe shapedRecipe) {
			packetByteBuf.writeVarInt(shapedRecipe.getWidth());
			packetByteBuf.writeVarInt(shapedRecipe.getHeight());
			packetByteBuf.writeString(shapedRecipe.getGroup());

			for (Ingredient ingredient : shapedRecipe.getIngredients()) {
				ingredient.write(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapedRecipe.getOutput());

			packetByteBuf.writeFloat(shapedRecipe.experience);
			packetByteBuf.writeVarInt(shapedRecipe.weavingTime);
		}
	}
}
