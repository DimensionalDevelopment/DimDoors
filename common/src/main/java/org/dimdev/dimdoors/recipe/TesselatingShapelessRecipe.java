package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class TesselatingShapelessRecipe implements TesselatingRecipe {
    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    final int weavingTime;

    public TesselatingShapelessRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients, int weavingTime) {
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
        this.weavingTime = weavingTime;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPELESS_TESSELATING.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != this.ingredients.size()) return false;
        else return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(input.getItem(0)) : input.stackedContents().canCraft(this, null);
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @Override
    public int weavingTime() {
        return weavingTime;
    }

    public static class Serializer implements RecipeSerializer<TesselatingShapelessRecipe> {
        private static final MapCodec<TesselatingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "")
                        .forGetter((arg) -> arg.group),
                ItemStack.STRICT_CODEC.fieldOf("result")
                        .forGetter((arg) -> arg.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients")
                        .flatXmap((list) -> {
                            Ingredient[] aingredient = list.toArray(Ingredient[]::new);
                            if (aingredient.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            } else
                                return aingredient.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: 9") : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                            }, DataResult::success)
                        .forGetter(arg -> arg.ingredients),
                        Codec.INT.optionalFieldOf("weaving_time", 200).forGetter(TesselatingRecipe::weavingTime))
                .apply(instance, TesselatingShapelessRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public Serializer() {
        }

        public MapCodec<TesselatingShapelessRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TesselatingShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll((arg2) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            var weavingTime = buffer.readVarInt();
            return new TesselatingShapelessRecipe(s, itemstack, nonnulllist, weavingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TesselatingShapelessRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());

            recipe.ingredients.forEach(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient));

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeVarInt(recipe.weavingTime());
        }
    }
}
