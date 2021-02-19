package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import net.minecraft.util.DyeColor;

public enum PocketColor {
	WHITE(0, DyeColor.WHITE),
	ORANGE(1, DyeColor.ORANGE),
	MAGENTA(2, DyeColor.MAGENTA),
	LIGHT_BLUE(3, DyeColor.LIGHT_BLUE),
	YELLOW(4, DyeColor.YELLOW),
	LIME(5, DyeColor.LIME),
	PINK(6, DyeColor.PINK),
	GRAY(7, DyeColor.GRAY),
	LIGHT_GRAY(8, DyeColor.LIGHT_GRAY),
	CYAN(9, DyeColor.CYAN),
	PURPLE(10, DyeColor.PURPLE),
	BLUE(11, DyeColor.BLUE),
	BROWN(12, DyeColor.BROWN),
	GREEN(13, DyeColor.GREEN),
	RED(14, DyeColor.RED),
	BLACK(15, DyeColor.BLACK),
	NONE(16, null);

	private final int id;
	private final DyeColor color;

	public static Codec<PocketColor> CODEC = Codec.INT.xmap(PocketColor::from, PocketColor::getId);

	PocketColor(int id, DyeColor color) {
		this.id = id;
		this.color = color;
	}

	public DyeColor getColor() {
		return this.color;
	}

	public Integer getId() {
		return this.id;
	}

	public static PocketColor from(DyeColor color) {
		for (PocketColor a : PocketColor.values()) {
			if (color == a.color) {
				return a;
			}
		}

		return NONE;
	}

	public static PocketColor from(int id) {
		for (PocketColor a : PocketColor.values()) {
			if (id == a.id) {
				return a;
			}
		}

		return NONE;
	}
}
