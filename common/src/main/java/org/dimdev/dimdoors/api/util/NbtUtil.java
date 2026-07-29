package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class NbtUtil {
    public static <T> T deserialize(CompoundTag data, MapCodec<T> codec) {
        return codec.decoder().parse(NbtOps.INSTANCE, data).getOrThrow();
    }

    public static <T> T deserialize(Tag data, Codec<T> codec) {
        return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow();
    }

    public static <T> CompoundTag serialize(CompoundTag tag, T data, MapCodec<T> codec) {
        return (CompoundTag) codec.encoder().encode(data, NbtOps.INSTANCE, tag).getOrThrow();
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
