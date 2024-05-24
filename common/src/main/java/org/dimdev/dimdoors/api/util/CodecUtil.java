package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.pockets.modifier.ResourceCodec;
import org.dimdev.dimdoors.util.Serialized;

import java.util.function.Function;
import java.util.function.Supplier;

public class CodecUtil {

    public static <T, U extends T, V extends T> Codec<T> xor(Codec<U> codec1, Codec<V> codec2) {
        return Codec.xor(codec1, codec2).xmap(either -> either.map(Function.identity(), Function.identity()), t -> null);
    }

    public static <T> Codec<T> resourceCodec(Codec<T> codec, Supplier<Codec<T>> supplier) {
        return xor(codec, Codec.lazyInitialized(() -> ResourceCodec.inputStream(stream -> ResourceUtil.JSON_READER.andThenReader(ResourceUtil.codec(JsonOps.INSTANCE, supplier.get())).apply(stream, null))));
    }

    public static <T extends Serialized<T>> Codec<T> registrarCodec(Registrar<Serialized.SerializedType<T>> registrar) {
        return ResourceLocation.CODEC.xmap(new Function<ResourceLocation, Serialized.SerializedType<T>>() {
            @Override
            public Serialized.SerializedType<T> apply(ResourceLocation id) {
                return registrar.get(id);
            }
        }, registrar::getId).dispatch(new Function<T, Serialized.SerializedType<T>>() {
            @Override
            public Serialized.SerializedType<T> apply(T t) {
                return t.getType();
            }
        }, new Function<Serialized.SerializedType<T>, MapCodec<? extends T>>() {
            @Override
            public MapCodec<? extends T> apply(Serialized.SerializedType<T> tSerializedType) {
                return tSerializedType.mapCodec();
            }
        });
    }

    public static <T extends Serialized<T>, V extends Serialized.SerializedType<T>> Codec<T> registrarCodec(Registrar<Serialized.SerializedType<T>> registrar, Supplier<Codec<T>> supplier) {
        return resourceCodec(registrarCodec(registrar), supplier);
    }
}
