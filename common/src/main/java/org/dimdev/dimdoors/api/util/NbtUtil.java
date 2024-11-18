package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class NbtUtil {
	public static <T> T deserialize(Tag data, Codec<T> codec) {
		return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow();
	}

	public static <T> Tag serialize(T data, Codec<T> codec) {
		return NbtOps.INSTANCE.withEncoder(codec).apply(data).getOrThrow();
	}

	public static CompoundTag asNbtCompound(Tag nbt, String error) {
		if (nbt == null || nbt.getId() == Tag.TAG_COMPOUND) {
			return (CompoundTag) nbt;
		}

		throw new RuntimeException(error);
	}
}
