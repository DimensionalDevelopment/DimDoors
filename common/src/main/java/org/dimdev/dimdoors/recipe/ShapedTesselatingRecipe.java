package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShapedTesselatingRecipe implements TesselatingRecipe {
    final ShapedRecipePattern pattern;
    final ItemStack result;
    final String group;
    final boolean showNotification;
    private final int weavingTime;

    public ShapedTesselatingRecipe(String group, ShapedRecipePattern pattern, ItemStack result, int weavingTime, boolean showNotification) {
        this.group = group;
        this.pattern = pattern;
        this.result = result;
        this.weavingTime = weavingTime;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPED_TESSELATING.get();
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.pattern.width() && height >= this.pattern.height();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level level) {
        return this.pattern.matches(inv);
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider provider) {
        return this.getResultItem(provider).copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getItems().length == 0);
    }

    public int weavingTime() {
        return weavingTime;
    }

    @Override
    public @NotNull RecipeType<TesselatingRecipe> getType() {
        return TesselatingRecipe.super.getType();
    }

    public static class Serializer implements RecipeSerializer<ShapedTesselatingRecipe> {
        public static final MapCodec<ShapedTesselatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> {
            return instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(a -> a.group),
                    ShapedRecipePattern.MAP_CODEC.forGetter(a -> a.pattern),
                    ItemStack.CODEC.fieldOf("result").forGetter(a -> a.result),
                    Codec.INT.optionalFieldOf("weaving_time", 200).forGetter(a -> a.weavingTime),
                    Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(a -> a.showNotification)
            ).apply(instance, ShapedTesselatingRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> STREAM_CODEC = StreamCodec.of(ShapedTesselatingRecipe.Serializer::toNetwork, ShapedTesselatingRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<ShapedTesselatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull ShapedTesselatingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            int weavingTime = buffer.readInt();
            boolean bl = buffer.readBoolean();
            return new ShapedTesselatingRecipe(string, pattern, itemStack, weavingTime, bl);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapedTesselatingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeInt(recipe.weavingTime);
            buffer.writeBoolean(recipe.showNotification);
        }
    }
}

