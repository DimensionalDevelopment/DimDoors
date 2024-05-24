package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public class TesselatingShapelessRecipe implements TesselatingRecipe {
    final NonNullList<Ingredient> ingredients;
    final ItemStack result;
    final String group;
    final boolean showNotification;
    private final int weavingTime;

    public TesselatingShapelessRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients, int weavingTime, boolean showNotification) {
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
        this.weavingTime = weavingTime;
        this.showNotification = showNotification;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPELESS_TESSELATING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SHAPELESS_TESSELATING.get();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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
    public boolean matches(TesselatingLoomBlockEntity inv, Level level) {
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
    public ItemStack assemble(TesselatingLoomBlockEntity craftingContainer, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<TesselatingShapelessRecipe> {

        private static final StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
        private static final MapCodec<TesselatingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter((shapelessRecipe) -> shapelessRecipe.group),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter((shapelessRecipe) -> shapelessRecipe.result), Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
            Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            return ingredients.length == 0 ? DataResult.error(() -> "No ingredients for shapeless recipe") : ingredients.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe") : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
        }, DataResult::success).forGetter((shapelessRecipe) -> shapelessRecipe.ingredients), Codec.INT.optionalFieldOf("weavingtime", 200).forGetter(TesselatingShapelessRecipe::weavingTime), Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(Recipe::showNotification)).apply(instance, TesselatingShapelessRecipe::new));

        private static TesselatingShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            int weavingTime = buffer.readInt();
            boolean bl = buffer.readBoolean();

            return new TesselatingShapelessRecipe(string, itemStack, nonNullList, weavingTime, bl);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TesselatingShapelessRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);            buffer.writeInt(recipe.weavingTime);
            buffer.writeBoolean(recipe.showNotification);
        }

        @Override
        public MapCodec<TesselatingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TesselatingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

