package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TesselatingShapelessRecipe implements TesselatingRecipe {
    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final int weavingTime;

    public TesselatingShapelessRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients, int weavingTime) {
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
        this.weavingTime = weavingTime;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPELESS_TESSELATING.get();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if (craftingInput.ingredientCount() != this.ingredients.size()) {
            return false;
        } else {
            return craftingInput.size() == 1 && this.ingredients.size() == 1 ? ((Ingredient)this.ingredients.getFirst()).test(craftingInput.getItem(0)) : craftingInput.stackedContents().canCraft(this, (IntList)null);
        }
    }

    @Override
    public int weavingTime() {
        return weavingTime;
    }


    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<TesselatingShapelessRecipe> {
        private static final MapCodec<TesselatingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter((shapelessRecipe) -> shapelessRecipe.group),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter((shapelessRecipe) -> shapelessRecipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                    Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                    return ingredients.length == 0 ? DataResult.error(() -> "No ingredients for shapeless recipe") : ingredients.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe") : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                    }, DataResult::success).forGetter(shapelessRecipe -> shapelessRecipe.ingredients),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weaving_time").forGetter(TesselatingShapelessRecipe::weavingTime))
                .apply(instance, TesselatingShapelessRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> STREAM_CODEC = StreamCodec.of(TesselatingShapelessRecipe.Serializer::toNetwork, TesselatingShapelessRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<TesselatingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TesselatingShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            int weavingTime = buffer.readInt();

            return new TesselatingShapelessRecipe(string, itemStack, nonNullList, weavingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TesselatingShapelessRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient));
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeInt(recipe.weavingTime);
        }
    }
}

