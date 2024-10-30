package org.dimdev.dimdoors.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShapedTesselatingRecipe implements TesselatingRecipe {
    final ShapedTesselatingRecipePattern pattern;
    final ItemStack result;
    final String group;
    final boolean showNotification;
    private final int weavingTime;

    public ShapedTesselatingRecipe(String group, ShapedTesselatingRecipePattern pattern, ItemStack result, int weavingTime, boolean showNotification) {
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
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
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
    public boolean matches(TesselatingContainer inv, Level level) {
        return this.pattern.matches(inv);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull TesselatingContainer container, @NotNull RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
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

    public static class Serializer implements RecipeSerializer<ShapedTesselatingRecipe> {
        public static final Codec<ShapedTesselatingRecipe> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(a -> a.group),
                    ShapedTesselatingRecipePattern.MAP_CODEC.forGetter(a -> a.pattern),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(a -> a.result),
                    ExtraCodecs.strictOptionalField(Codec.INT, "weaving_time", 200).forGetter(a -> a.weavingTime),
                    ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(a -> a.showNotification)
            ).apply(instance, ShapedTesselatingRecipe::new);
        });

        @Override
        public Codec<ShapedTesselatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull ShapedTesselatingRecipe fromNetwork(FriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            ShapedTesselatingRecipePattern pattern = ShapedTesselatingRecipePattern.fromNetwork(buffer);
            ItemStack itemStack = buffer.readItem();
            int weavingTime = buffer.readInt();
            boolean bl = buffer.readBoolean();
            return new ShapedTesselatingRecipe(string, pattern, itemStack, weavingTime, bl);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedTesselatingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.pattern.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.weavingTime);
            buffer.writeBoolean(recipe.showNotification);
        }
    }
}

