package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.fabricmc.fabric.api.util.NbtType;

public class NbtUtil {
	public static <T> T deserialize(NbtElement data, Codec<T> codec) {
		return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static <T> NbtElement serialize(T data, Codec<T> codec) {
		return NbtOps.INSTANCE.withEncoder(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static NbtCompound asNbtCompound(NbtElement nbt, String error) {
		if (nbt == null || nbt.getType() == NbtType.COMPOUND) {
			return (NbtCompound) nbt;
		}

		throw new RuntimeException(error);
	}
}
