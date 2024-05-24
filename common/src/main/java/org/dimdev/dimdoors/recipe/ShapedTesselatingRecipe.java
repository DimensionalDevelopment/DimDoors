package org.dimdev.dimdoors.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

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

    public ShapedTesselatingRecipe(String group, ShapedRecipePattern pattern, ItemStack result, int weavingTime) {
        this(group, pattern, result, weavingTime, true);
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
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.SHAPED_TESSELATING.get();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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
    public boolean matches(TesselatingLoomBlockEntity inv, Level level) {
        return this.pattern.matches(inv);
    }

    public @NotNull NonNullList<ItemStack> getRemainingItems(TesselatingLoomBlockEntity container) {
        NonNullList<ItemStack> nonNullList = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 1; i < nonNullList.size(); ++i) {
            Item item = container.getItem(i).getItem();
            if (!item.hasCraftingRemainingItem()) continue;
            nonNullList.set(i, new ItemStack(item.getCraftingRemainingItem()));
        }
        return nonNullList;
    }

    @Override
    public ItemStack assemble(TesselatingLoomBlockEntity craftingContainer, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    public static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        HashSet<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");
        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }
                set.remove(string);
                nonNullList.set(j + patternWidth * i, ingredient);
            }
        }
        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        return nonNullList;
    }

    @VisibleForTesting
    public static String[] shrink(String ... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < toShrink.length; ++m) {
            String string = toShrink[m];
            i = Math.min(i, firstNonSpace(string));
            int n = lastNonSpace(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }
                ++l;
                continue;
            }
            l = 0;
        }
        if (toShrink.length == l) {
            return new String[0];
        }
        String[] strings = new String[toShrink.length - l - k];
        for (int o = 0; o < strings.length; ++o) {
            strings[o] = toShrink[o + k].substring(i, j + 1);
        }
        return strings;
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getItems().length == 0);
    }

    private static int firstNonSpace(String entry) {
        int i;
        for (i = 0; i < entry.length() && entry.charAt(i) == ' '; ++i) {
        }
        return i;
    }

    private static int lastNonSpace(String entry) {
        int i;
        for (i = entry.length() - 1; i >= 0 && entry.charAt(i) == ' '; --i) {
        }
        return i;
    }

    public int weavingTime() {
        return weavingTime;
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    public static class Serializer implements RecipeSerializer<ShapedTesselatingRecipe> {
        public static final MapCodec<ShapedTesselatingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedTesselatingRecipe::getGroup),
                    ShapedRecipePattern.MAP_CODEC.forGetter(ShapedTesselatingRecipe::getPattern),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter((shapedRecipe) -> shapedRecipe.result),
                    Codec.INT.optionalFieldOf("weavingtime", 200).forGetter(ShapedTesselatingRecipe::weavingTime),
                    Codec.BOOL.optionalFieldOf("show_notification", true).forGetter((shapedRecipe) -> shapedRecipe.showNotification))
                    .apply(instance, ShapedTesselatingRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        private static ShapedTesselatingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            var pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
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

        @Override
        public MapCodec<ShapedTesselatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedTesselatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

