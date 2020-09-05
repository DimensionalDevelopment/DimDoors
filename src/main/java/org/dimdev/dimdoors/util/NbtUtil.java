package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class NbtUtil {
    public static <T> T deserialize(Tag data, Codec<T> codec) {
        return NbtOps.INSTANCE.withParser(codec).apply(data).getOrThrow(true, a -> {});
    }

    public static <T> Tag serialize(T data, Codec<T> codec) {
        return NbtOps.INSTANCE.withEncoder(codec).apply(data).getOrThrow(true, a -> {});
    }
}
