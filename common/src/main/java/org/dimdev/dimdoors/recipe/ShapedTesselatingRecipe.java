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

    public ShapedTesselatingRecipe(String group, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, int weavingTime) {
        this.group = group;
        this.pattern = pattern;
        this.result = result;
        this.showNotification = showNotification;
        this.weavingTime = weavingTime;
    }

    public ShapedTesselatingRecipe(String group, ShapedRecipePattern pattern, ItemStack result, int weavingTime) {
        this(group, pattern, result, true, weavingTime);
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
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
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
    public @NotNull ItemStack assemble(@NotNull CraftingInput container, @NotNull HolderLookup.Provider registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream().filter((ingredient) -> !ingredient.isEmpty()).anyMatch((ingredient) -> ingredient.getItems().length == 0);
    }

    public int weavingTime() {
        return weavingTime;
    }

    public static class Serializer implements RecipeSerializer<ShapedTesselatingRecipe> {
        public static final MapCodec<ShapedTesselatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "")
                        .forGetter(shapedRecipe -> shapedRecipe.group),
                ShapedRecipePattern.MAP_CODEC
                        .forGetter(shapedRecipe -> shapedRecipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result")
                        .forGetter(shapedRecipe -> shapedRecipe.result),
                Codec.BOOL.optionalFieldOf("show_notification", true)
                        .forGetter(shapedRecipe -> shapedRecipe.showNotification),
                Codec.INT.optionalFieldOf("weaving_time", 200)
                        .forGetter(shapedRecipe -> shapedRecipe.weavingTime))
                .apply(instance, ShapedTesselatingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);


        @Override
        public MapCodec<ShapedTesselatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ShapedTesselatingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            var string = buffer.readUtf();
            var shapedRecipePattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            var itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            var bl = buffer.readBoolean();
            var weavingTime = buffer.readVarInt();
            return new ShapedTesselatingRecipe(string,  shapedRecipePattern, itemStack, bl, weavingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapedTesselatingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.showNotification);
            buffer.writeVarInt(recipe.weavingTime);
        }
    }
}

