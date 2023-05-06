package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.Tag;

public class NbtUtil {
	public static <T> T deserialize(NbtElement data, Codec<T> codec) {
		return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static <T> NbtElement serialize(T data, Codec<T> codec) {
		return NbtOps.INSTANCE.withEncoder(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static CompoundTag asNbtCompound(Tag nbt, String error) {
		if (nbt == null || nbt.getId() == Tag.TAG_COMPOUND) {
			return (CompoundTag) nbt;
		}

		throw new RuntimeException(error);
	}
}
