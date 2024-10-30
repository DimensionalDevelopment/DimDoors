package org.dimdev.dimdoors.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record ShapedTesselatingRecipePattern(int width, int height, NonNullList<Ingredient> ingredients, Optional<ShapedTesselatingRecipePattern.Data> data) {
    private static final int MAX_SIZE = 3;
    public static final MapCodec<ShapedTesselatingRecipePattern> MAP_CODEC;


    public static ShapedTesselatingRecipePattern of(Map<Character, Ingredient> map, String... strings) {
        return of(map, List.of(strings));
    }

    public static ShapedTesselatingRecipePattern of(Map<Character, Ingredient> map, List<String> list) {
        Data data = new Data(map, list);
        return Util.getOrThrow(unpack(data), IllegalArgumentException::new);
    }

    private static DataResult<ShapedTesselatingRecipePattern> unpack(Data data) {
        String[] strings = shrink(data.pattern);
        int i = strings[0].length();
        int j = strings.length;
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);
        CharSet charSet = new CharArraySet(data.key.keySet());

        for(int k = 0; k < strings.length; ++k) {
            String string = strings[k];

            for(int l = 0; l < string.length(); ++l) {
                char c = string.charAt(l);
                Ingredient ingredient = c == ' ' ? Ingredient.EMPTY : data.key.get(c);
                if (ingredient == null) {
                    return DataResult.error(() -> {
                        return "Pattern references symbol '" + c + "' but it's not defined in the key";
                    });
                }

                charSet.remove(c);
                nonNullList.set(l + i * k, ingredient);
            }
        }

        if (!charSet.isEmpty()) {
            return DataResult.error(() -> {
                return "Key defines symbols that aren't used in pattern: " + charSet;
            });
        } else {
            return DataResult.success(new ShapedTesselatingRecipePattern(i, j, nonNullList, Optional.of(data)));
        }
    }

    @VisibleForTesting
    static String[] shrink(List<String> list) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < list.size(); ++m) {
            String string = list.get(m);
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

        if (list.size() == l) {
            return new String[0];
        } else {
            String[] strings = new String[list.size() - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = list.get(o + k).substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int firstNonSpace(String string) {
        int i;
        for(i = 0; i < string.length() && string.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String string) {
        int i;
        for(i = string.length() - 1; i >= 0 && string.charAt(i) == ' '; --i) {
        }

        return i;
    }

    public boolean matches(TesselatingContainer craftingContainer) {
        for(int i = 0; i <= craftingContainer.getWidth() - this.width; ++i) {
            for(int j = 0; j <= craftingContainer.getHeight() - this.height; ++j) {
                if (this.matches(craftingContainer, i, j, true)) {
                    return true;
                }

                if (this.matches(craftingContainer, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(TesselatingContainer craftingContainer, int i, int j, boolean bl) {
        for(int k = 0; k < craftingContainer.getWidth(); ++k) {
            for(int l = 0; l < craftingContainer.getHeight(); ++l) {
                int m = k - i;
                int n = l - j;
                Ingredient ingredient = Ingredient.EMPTY;
                if (m >= 0 && n >= 0 && m < this.width && n < this.height) {
                    if (bl) {
                        ingredient = this.ingredients.get(this.width - m - 1 + n * this.width);
                    } else {
                        ingredient = this.ingredients.get(m + n * this.width);
                    }
                }

                if (!ingredient.test(craftingContainer.getItem(k + l * craftingContainer.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.width);
        friendlyByteBuf.writeVarInt(this.height);

        for (Ingredient ingredient : this.ingredients) {
            ingredient.toNetwork(friendlyByteBuf);
        }

    }

    public static ShapedTesselatingRecipePattern fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        int i = friendlyByteBuf.readVarInt();
        int j = friendlyByteBuf.readVarInt();
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);
        nonNullList.replaceAll((ingredient) -> {
            return Ingredient.fromNetwork(friendlyByteBuf);
        });
        return new ShapedTesselatingRecipePattern(i, j, nonNullList, Optional.empty());
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public NonNullList<Ingredient> ingredients() {
        return this.ingredients;
    }

    public Optional<ShapedTesselatingRecipePattern.Data> data() {
        return this.data;
    }

    static {
        MAP_CODEC = Data.MAP_CODEC.flatXmap(ShapedTesselatingRecipePattern::unpack, (shapedRecipePattern) -> shapedRecipePattern.data().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));
    }

    public record Data(Map<Character, Ingredient> key, List<String> pattern) {
        private static final Codec<List<String>> PATTERN_CODEC;
        private static final Codec<Character> SYMBOL_CODEC;
        public static final MapCodec<ShapedTesselatingRecipePattern.Data> MAP_CODEC;

        static {
            PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap((list) -> {
                if (list.size() > 3) {
                    return DataResult.error(() -> {
                        return "Invalid pattern: too many rows, 3 is maximum";
                    });
                } else if (list.isEmpty()) {
                    return DataResult.error(() -> {
                        return "Invalid pattern: empty pattern not allowed";
                    });
                } else {
                    int i = list.get(0).length();
                    Iterator<String> var2 = list.iterator();

                    String string;
                    do {
                        if (!var2.hasNext()) {
                            return DataResult.success(list);
                        }

                        string = (String)var2.next();
                        if (string.length() > 3) {
                            return DataResult.error(() -> {
                                return "Invalid pattern: too many columns, 3 is maximum";
                            });
                        }
                    } while(i == string.length());

                    return DataResult.error(() -> {
                        return "Invalid pattern: each row must be the same width";
                    });
                }
            }, Function.identity());
            SYMBOL_CODEC = Codec.STRING.comapFlatMap((string) -> {
                if (string.length() != 1) {
                    return DataResult.error(() -> "Invalid key entry: '" + string + "' is an invalid symbol (must be 1 character only).");
                } else {
                    return " ".equals(string) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(string.charAt(0));
                }
            }, String::valueOf);
            MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(Data::key),
                    PATTERN_CODEC.fieldOf("pattern").forGetter(Data::pattern))
                    .apply(instance, Data::new));
        }
    }
}
