package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
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
    public boolean matches(TesselatingContainer inv, Level level) {
        StackedContents stackedContents = new StackedContents();
        int i = 0;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemStack = inv.getItem(j);
            if (itemStack.isEmpty()) continue;
            ++i;
            stackedContents.accountStack(itemStack, 1);
        }
        return i == this.ingredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public int weavingTime() {
        return weavingTime;
    }


    @Override
    public ItemStack assemble(TesselatingContainer container, HolderLookup.Provider registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<TesselatingShapelessRecipe> {
        private static final Codec<TesselatingShapelessRecipe> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((shapelessRecipe) -> shapelessRecipe.group),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((shapelessRecipe) -> shapelessRecipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                    Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                    return ingredients.length == 0 ? DataResult.error(() -> "No ingredients for shapeless recipe") : ingredients.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe") : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                    }, DataResult::success).forGetter(shapelessRecipe -> shapelessRecipe.ingredients),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weaving_time").forGetter(TesselatingShapelessRecipe::weavingTime))
                .apply(instance, TesselatingShapelessRecipe::new));


        @Override
        public Codec<TesselatingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public TesselatingShapelessRecipe fromNetwork(FriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ItemStack itemStack = buffer.readItem();
            int weavingTime = buffer.readInt();

            return new TesselatingShapelessRecipe(string, itemStack, nonNullList, weavingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TesselatingShapelessRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> ingredient.toNetwork(buffer));
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.weavingTime);
        }
    }
}

