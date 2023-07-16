package org.dimdev.dimdoors.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Map;

public class TesselatingRecipe extends ShapedRecipe {
	public final float experience;
	public final int weavingTime;

	public TesselatingRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> defaultedList, ItemStack itemStack, float experience, int weavingTime) {
		super(id, group, CraftingBookCategory.MISC, width, height, defaultedList, itemStack);

		this.experience = experience;
		this.weavingTime = weavingTime;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeSerializers.TESSELATING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.TESSELATING.get();
	}

	public static class Serializer implements RecipeSerializer<TesselatingRecipe> {

		@Override
		public TesselatingRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			String string = GsonHelper.getAsString(jsonObject, "group", "");
			Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
			String[] strings = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
			int i = strings[0].length();
			int j = strings.length;
			NonNullList<Ingredient> defaultedList = ShapedRecipe.dissolvePattern(strings, map, i, j);
			ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
			float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
			int weavingTime = GsonHelper.getAsInt(jsonObject, "weavingtime", 200);

			return new TesselatingRecipe(id, string, i, j, defaultedList, itemStack, experience, weavingTime);
		}


		@Override
		public TesselatingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			int j = packetByteBuf.readVarInt();
			String string = packetByteBuf.readUtf();
			NonNullList<Ingredient> defaultedList = NonNullList.withSize(i * j, Ingredient.EMPTY);

			for(int k = 0; k < defaultedList.size(); ++k) {
				defaultedList.set(k, Ingredient.fromNetwork(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItem();

			float experience = packetByteBuf.readFloat();

			int weavingTime = packetByteBuf.readVarInt();

			return new TesselatingRecipe(id, string, i, j, defaultedList, itemStack, experience, weavingTime);
		}

		@Override
		public void toNetwork(FriendlyByteBuf packetByteBuf, TesselatingRecipe shapedRecipe) {
			packetByteBuf.writeVarInt(shapedRecipe.getWidth());
			packetByteBuf.writeVarInt(shapedRecipe.getHeight());
			packetByteBuf.writeUtf(shapedRecipe.getGroup());

			for (Ingredient ingredient : shapedRecipe.getIngredients()) {
				ingredient.toNetwork(packetByteBuf);
			}

			packetByteBuf.writeItem(shapedRecipe.getResultItem(null));

			packetByteBuf.writeFloat(shapedRecipe.experience);
			packetByteBuf.writeVarInt(shapedRecipe.weavingTime);
		}
	}
}
