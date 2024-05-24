package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.util.ResourceCodec;

import java.util.function.Function;
import java.util.function.Supplier;

public class CodecUtil {

    public static <T, U extends T, V extends T> Codec<T> xor(Codec<U> codec1, Codec<V> codec2) {
        return Codec.xor(codec1, codec2).xmap(either -> either.map(Function.identity(), Function.identity()), t -> null);
    }

    public static <T> Codec<T> resourceCodec(String startingPath, Codec<T> codec, Supplier<Codec<T>> supplier) {
        return xor(codec, Codec.lazyInitialized(() -> ResourceCodec.inputStream(startingPath,stream -> ResourceUtil.JSON_READER.andThenReader(ResourceUtil.codec(JsonOps.INSTANCE, supplier.get())).apply(stream, null))));
    }

    public static <T, U> Codec<U> registrarCodec(Registrar<T> registrar, Function<U, T> function1, Function<T, MapCodec<? extends U>> function2) {
        return  ResourceLocation.CODEC.xmap(registrar::get, registrar::getId).dispatch(function1, function2);
    }

    public static <T, U> Codec<U> registrarCodec(String startingPath, Registrar<T> registrar, Function<U, T> function1, Function<T, MapCodec<? extends U>> function2, Supplier<Codec<U>> supplier) {
        return resourceCodec(startingPath, registrarCodec(registrar, function1, function2), supplier);
    }
}
