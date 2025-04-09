package org.dimdev.dimdoors.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CodecUtils {
    public static <T, V, U extends Map<T, V>> Codec<U> listMap(Codec<T> keyCodec, Codec<V> valueCodec, Supplier<U> supplier) {
        return Codec.pair(keyCodec, valueCodec).listOf().<U>xmap(pairs -> {
            var map = supplier.get();
            pairs.forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));

            return map;
        }, map -> map.entrySet().stream().map(a -> Pair.of(a.getKey(), a.getValue())).toList());

    }

    public static ResourceManager manager;

    public static <T> Codec<T> reference(Function<Path<String>, @Nullable T> function) {
        return Codec.STRING.flatXmap(s -> DataResult.success(function.apply(Path.stringPath(s))), t -> DataResult.error(() -> "Serialization of modifier reference not supported."));
    }

    public static <T> Codec<T> codecWithReference(Codec<T> base, String path) {
//        Codec<T> reference = ResourceLocation.CODEC.flatXmap((Function<ResourceLocation, DataResult<ResourceLocation>>) resourceLocation -> DataResult.success(resourceLocation.withPrefix(path + "/").withSuffix(".json")), resourceLocation -> DataResult.error(() -> "")).flatXmap(resourceLocation -> ResourceUtil.loadResource(manager, resourceLocation, ResourceUtil.JSON_READER.andThenComposable(json -> {
//            var result = JsonOps.INSTANCE.withParser(base).apply(json);
//
//            result.ifError(new Consumer<DataResult.Error<T>>() {
//                @Override
//                public void accept(DataResult.Error<T> tError) {
//                    System.out.println("Blarge: -> " + tError.message());
//                }
//            });
//
//            return result;
//        })), t -> DataResult.error(() -> ""));
//
//        return Codec.withAlternative(base, reference);

        return Codec.PASSTHROUGH.flatXmap(new Function<Dynamic<?>, DataResult<? extends T>>() {
            @Override
            public DataResult<? extends T> apply(Dynamic<?> dynamic) {
                var optional = dynamic.asString().flatMap(ResourceLocation::read).map(a -> a.withSuffix(".json").withPrefix(path)).flatMap(resourceLocation -> {
                    return ResourceUtil.loadResource(manager, resourceLocation, ResourceUtil.JSON_READER.andThenComposable(json -> JsonOps.INSTANCE.withParser(base).apply(json)));
                });

                if (optional.isSuccess()) {
                    return optional;
                }

                try {
                    return base.parse(dynamic);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Function<T, DataResult<? extends Dynamic<?>>>() {
            @Override
            public DataResult<? extends Dynamic<?>> apply(T t) {
                return DataResult.error(() -> "Blep");
            }
        });
    }

}
