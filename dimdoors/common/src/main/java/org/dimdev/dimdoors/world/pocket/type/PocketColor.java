package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.tag.ModItemTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PocketColor implements StringRepresentable {
    WHITE("white", DyeColor.WHITE),
    ORANGE("orange", DyeColor.ORANGE),
    MAGENTA("magenta", DyeColor.MAGENTA),
    LIGHT_BLUE("light_blue", DyeColor.LIGHT_BLUE),
    YELLOW("yellow", DyeColor.YELLOW),
    LIME("lime", DyeColor.LIME),
    PINK("pink", DyeColor.PINK),
    GRAY("gray", DyeColor.GRAY),
    LIGHT_GRAY("light_gray", DyeColor.LIGHT_GRAY),
    CYAN("cyan", DyeColor.CYAN),
    PURPLE("purple", DyeColor.PURPLE),
    BLUE("blue", DyeColor.BLUE),
    BROWN("brown", DyeColor.BROWN),
    GREEN("green", DyeColor.GREEN),
    RED("red", DyeColor.RED),
    BLACK("black", DyeColor.BLACK),
    NONE("none", null);

    private final String id;
    private final DyeColor color;

    public static Codec<PocketColor> CODEC = StringRepresentable.fromValues(PocketColor::values);
    public static StreamCodec<RegistryFriendlyByteBuf, PocketColor> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, PocketColor>() {
        @Override
        public @NotNull PocketColor decode(RegistryFriendlyByteBuf buffer) {
            return buffer.readEnum(PocketColor.class);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, @NotNull PocketColor pocketColor) {
            buffer.writeEnum(pocketColor);
        }
    };

    PocketColor(String name, DyeColor color) {
    this.id = name;
    this.color = color;
    }

    public static @Nullable DyeColor from(ItemStack stack) {
        for(var dyColor: DyeColor.values()) {
            var tag = ModItemTags.DYES.get(dyColor);
            if(tag != null && stack.is(tag)) return dyColor;
        }

        return null;

    }

    public DyeColor getColor() {
    return this.color;
    }

    public static PocketColor from(DyeColor color) {
    for (PocketColor a : PocketColor.values()) {
        if (color == a.color) {
        return a;
        }
    }

    return NONE;
    }

    @Override
    public @NotNull String getSerializedName() {
    return id;
    }
}
