package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;

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

    PocketColor(String name, DyeColor color) {
    this.id = name;
    this.color = color;
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
    public String getSerializedName() {
    return id;
    }
}
