package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import net.fabricmc.fabric.api.util.NbtType;

public class NbtUtil {
	public static <T> T deserialize(Tag data, Codec<T> codec) {
		return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static <T> Tag serialize(T data, Codec<T> codec) {
		return NbtOps.INSTANCE.withEncoder(codec).apply(data).getOrThrow(true, a -> {
		});
	}

	public static CompoundTag asCompoundTag(Tag tag, String error) {
		if (tag == null || tag.getType() == NbtType.COMPOUND) {
			return (CompoundTag) tag;
		}

		throw new RuntimeException(error);
	}
}
