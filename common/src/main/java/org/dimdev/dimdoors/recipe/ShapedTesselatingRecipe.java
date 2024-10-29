package org.dimdev.dimdoors.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe.Serializer.SINGLE_CHARACTER_STRING_CODEC;

public class ShapedTesselatingRecipe implements TesselatingRecipe {
    private final int width;
    final int height;
    final NonNullList<Ingredient> recipeItems;
    final ItemStack result;
    final String group;
    final boolean showNotification;
    private final int weavingTime;

    public ShapedTesselatingRecipe(String group, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result, int weavingTime, boolean showNotification) {
        this.group = group;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
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
        return this.recipeItems;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(TesselatingLoomBlockEntity inv, Level level) {
        for (int i = 0; i <= 3 - this.width; ++i) {
            for (int j = 0; j <= 3 - this.height; ++j) {
                if (this.matches(inv, i, j, true)) {
                    return true;
                }
                if (this.matches(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean matches(TesselatingLoomBlockEntity craftingInventory, int width, int height, boolean mirrored) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int k = x - width;
                int l = y - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    ingredient = mirrored ? this.recipeItems.get(this.width - k - 1 + l * this.width) : this.recipeItems.get(k + l * this.width);
                }

                if (!ingredient.test(craftingInventory.getItem(x + y * 3))) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public @NotNull ItemStack assemble(@NotNull TesselatingLoomBlockEntity container, @NotNull RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }


    @VisibleForTesting
    static String[] shrink(List<String> recipe) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < recipe.size(); ++m) {
            String string = recipe.get(m);
            i = Math.min(i, firstNonSpace(string));
            int n = lastNonSpace(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (recipe.size() == l) {
            return new String[0];
        } else {
            String[] strings = new String[recipe.size() - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = recipe.get(o + k).substring(i, j + 1);
            }

            return strings;
        }
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

    public static ItemStack itemStackFromJson(JsonObject stackObject) {
        Item item = itemFromJson(stackObject);
        if (stackObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        int i = GsonHelper.getAsInt(stackObject, "count", 1);
        if (i < 1) {
            throw new JsonSyntaxException("Invalid output count: " + i);
        }
        return new ItemStack(item, i);
    }

    public static Item itemFromJson(JsonObject itemObject) {
        String string = GsonHelper.getAsString(itemObject, "item");
        Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + string);
        }
        return item;
    }

    public int weavingTime() {
        return weavingTime;
    }

    public static class Serializer implements RecipeSerializer<ShapedTesselatingRecipe> {
        public static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().flatXmap((list) -> {
            if (list.size() > 3) {
                return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
            } else if (list.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            } else {
                int expectedLength = list.get(0).length();

                for (String string : list) {
                    if (string.length() > 3) {
                        return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
                    }
                    if (string.length() != expectedLength) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }

                return DataResult.success(list);
            }
        }, DataResult::success);

        public static final Codec<String> SINGLE_CHARACTER_STRING_CODEC = Codec.STRING.flatXmap((string) -> {
            if (string.length() != 1) {
                return DataResult.error(() -> {
                    return "Invalid key entry: '" + string + "' is an invalid symbol (must be 1 character only).";
                });
            } else {
                return " ".equals(string) ? DataResult.error(() -> {
                    return "Invalid key entry: ' ' is a reserved symbol.";
                }) : DataResult.success(string);
            }
        }, DataResult::success);

        public static final Codec<ShapedTesselatingRecipe> CODEC = RawShapedRecipe.CODEC.flatXmap((rawShapedRecipe) -> {
            String[] strings = shrink(rawShapedRecipe.pattern);
            int i = strings[0].length();
            int j = strings.length;
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);
            Set<String> set = Sets.newHashSet(rawShapedRecipe.key.keySet());

            for(int k = 0; k < strings.length; ++k) {
                String string = strings[k];

                for(int l = 0; l < string.length(); ++l) {
                    String string2 = string.substring(l, l + 1);
                    Ingredient ingredient = string2.equals(" ") ? Ingredient.EMPTY : rawShapedRecipe.key.get(string2);
                    if (ingredient == null) {
                        return DataResult.error(() -> "Pattern references symbol '" + string2 + "' but it's not defined in the key");
                    }

                    set.remove(string2);
                    nonNullList.set(l + i * k, ingredient);
                }
            }

            if (!set.isEmpty()) {
                return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + set);
            } else {
                ShapedTesselatingRecipe shapedRecipe = new ShapedTesselatingRecipe(rawShapedRecipe.group(), i, j, nonNullList, rawShapedRecipe.result(),rawShapedRecipe.weavingTime(), rawShapedRecipe.showNotification());
                return DataResult.success(shapedRecipe);
            }
        }, (shapedRecipe) -> {
            throw new NotImplementedException("Serializing ShapedRecipe is not implemented yet.");
        });

        @Override
        public Codec<ShapedTesselatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull ShapedTesselatingRecipe fromNetwork(FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String string = buffer.readUtf();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);
            nonNullList.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ItemStack itemStack = buffer.readItem();
            int weavingTime = buffer.readInt();
            boolean bl = buffer.readBoolean();
            return new ShapedTesselatingRecipe(string, i, j, nonNullList, itemStack, weavingTime, bl);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedTesselatingRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeUtf(recipe.group);
            recipe.recipeItems.forEach(ingredient -> ingredient.toNetwork(buffer));
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.weavingTime);
            buffer.writeBoolean(recipe.showNotification);
        }
    }

    private record RawShapedRecipe(String group, Map<String, Ingredient> key, List<String> pattern, ItemStack result, boolean showNotification, int weavingTime) {

        public static final Codec<RawShapedRecipe> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(RawShapedRecipe::group),
                ExtraCodecs.strictUnboundedMap(SINGLE_CHARACTER_STRING_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(RawShapedRecipe::key),
                Serializer.PATTERN_CODEC.fieldOf("pattern").forGetter(RawShapedRecipe::pattern),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(RawShapedRecipe::result),
                ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(RawShapedRecipe::showNotification),
                ExtraCodecs.strictOptionalField(ExtraCodecs.NON_NEGATIVE_INT, "weavingTime", 0).forGetter(RawShapedRecipe::weavingTime))
                .apply(instance, RawShapedRecipe::new));
    }
}

