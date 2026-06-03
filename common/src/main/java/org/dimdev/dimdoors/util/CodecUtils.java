package org.dimdev.dimdoors.util;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.world.decay.conditions.GenericDecayCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
//        Codec<T> reference = Identifier.CODEC.flatXmap((Function<Identifier, DataResult<Identifier>>) Identifier -> DataResult.success(Identifier.withPrefix(path + "/").withSuffix(".json")), Identifier -> DataResult.error(() -> "")).flatXmap(Identifier -> ResourceUtil.loadResource(manager, Identifier, ResourceUtil.JSON_READER.andThenComposable(json -> {
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

//        return Codec.PASSTHROUGH.flatXmap(new Function<Dynamic<?>, DataResult<? extends T>>() {
//            @Override
//            public DataResult<? extends T> apply(Dynamic<?> dynamic) {
//                var optional = dynamic.asString().flatMap(Identifier::read).map(a -> a.withSuffix(".json").withPrefix(path)).flatMap(Identifier -> {
//                    return ResourceUtil.loadResource(manager, Identifier, ResourceUtil.JSON_READER.andThenComposable(json -> JsonOps.INSTANCE.withParser(base).apply(json)));
//                });
//
//                if (optional.isSuccess()) {
//                    return optional;
//                }
//
//                try {
//                    return base.parse(dynamic);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, new Function<T, DataResult<? extends Dynamic<?>>>() {
//            @Override
//            public DataResult<? extends Dynamic<?>> apply(T t) {
//                return base.encodeStart(JsonOps.INSTANCE, t).map(a -> new Dynamic<>(JsonOps.INSTANCE, a));
//            }
//        });

        return Codec.PASSTHROUGH.flatXmap(
                dynamic -> {
                    var stringResult = dynamic.asString().result();
                    if (stringResult.isPresent()) {
                        var locationResult = Identifier.read(stringResult.get()).resultOrPartial(a -> System.out.println("Error location not found: " + a));
                        if (locationResult.isPresent()) {
                            var Identifier = locationResult.get().withSuffix(".json").withPrefix(path);
                            var loaded = ResourceUtil.loadResource(manager, Identifier, ResourceUtil.JSON_READER.andThenComposable(json -> JsonOps.INSTANCE.withParser(base).apply(json).ifError(a -> System.out.println("Error with " + Identifier + ": " + a.message()))));
                            if (loaded != null && loaded.isSuccess()) return loaded;
                        }
                    }

                    try {
                        return base.parse(dynamic);
                    } catch (Exception e) {
                        return DataResult.error(() -> "Fallback parse failure: " + e.getMessage());
                    }
                },
                t -> base.encodeStart(JsonOps.INSTANCE, t).map(json -> new Dynamic<>(JsonOps.INSTANCE, json))
        );
    }

    public static <T> Codec.ResultFunction<T> debugResultFunction(Function<T, String> onSuccess, Function<String, String> onError) {
        return new Codec.ResultFunction<>() {
            @Override
            public <D> DataResult<Pair<T, D>> apply(DynamicOps<D> ops, D input, DataResult<Pair<T, D>> result) {
                result.result().ifPresent(pair -> {
                    System.err.println(onSuccess.apply(pair.getFirst()));
                });
                result.error().ifPresent(error -> {
                    System.err.println(onError.apply(error.message()));
                });
                return result;
            }

            @Override
            public <D> DataResult<D> coApply(DynamicOps<D> ops, T input, DataResult<D> result) {
                return result;
            }
        };
    }

    public static <T extends GenericDecayCondition<?>, V> Products.P2<RecordCodecBuilder.Mu<T>, TagOrElementLocation<V>, Boolean> decayConditionFields(RecordCodecBuilder.Instance<T> instance, ResourceKey<Registry<V>> key) {
        return instance.group(
                TagOrElementLocation.codec(key).fieldOf("entry").forGetter(t -> (TagOrElementLocation<V>) t.getTagOrElementLocation()),
                Codec.BOOL.optionalFieldOf("invert", false).forGetter(GenericDecayCondition::invert)
        );
    }

    public static <T extends GenericDecayCondition<?>, V> MapCodec<T> createCodec(BiFunction<TagOrElementLocation<V>, Boolean, T> function, ResourceKey<Registry<V>> key) {
        return RecordCodecBuilder.mapCodec(instance -> decayConditionFields(instance, key).apply(instance, function));
    }

    public static <T> Codec<T> mapResult(Codec<T> baseCodec, Function<Dynamic<?>, Dynamic<?>> function) {
        return Codec.PASSTHROUGH.<Dynamic<?>>xmap(function, Function.identity()).flatXmap(baseCodec::parse, t -> baseCodec.encodeStart(JsonOps.INSTANCE, t).map(a -> new Dynamic<>(JsonOps.INSTANCE, a)));
    }

    public static <K, V, M extends Map<K, V>> Codec<M> unboundedMap(Codec<K> keyCodec, Codec<V> valueCodec, Function<Map<K, V>, M> mapMFunction) {
        return Codec.unboundedMap(keyCodec, valueCodec).xmap(mapMFunction, Function.identity());
    }

    public static <K, V> Codec<Map<K, V>> unboundedMap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(keyCodec, valueCodec).xmap(Maps::newHashMap, Function.identity());
    }

    public static final class TagOrElementLocation<T> {
        private TagKey<T> tag;
        private ResourceKey<T> key;

        public static <T> Codec<TagOrElementLocation<T>> codec(ResourceKey<Registry<T>> key) {
            return Codec.STRING.comapFlatMap(string -> string.startsWith("#") ? Identifier.read(string.substring(1)).map(Identifier -> new TagOrElementLocation<>(Identifier, true, key)) : Identifier.read(string).map(Identifier -> new TagOrElementLocation<T>(Identifier, false, key)), TagOrElementLocation::decoratedId);
        }

        public static <T> TagOrElementLocation<T> of(TagKey<T> tag, ResourceKey<Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.location(), true, registry);
        }

        public static <T> TagOrElementLocation<T> of(ResourceKey<T> tag, ResourceKey<Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.identifier(), false, registry);
        }

        public TagOrElementLocation(Identifier id, boolean tag, ResourceKey<Registry<T>> registryResourceKey) {
            if(tag) this.tag = TagKey.create(registryResourceKey, id);
            else this.key = ResourceKey.create(registryResourceKey, id);
        }

        @Override
            public String toString() {
                return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag != null ? "#" + tag.location() : this.key.identifier().toString();
        }

        public boolean test(Holder<T> holder) {
            return tag != null && holder.is(tag) || holder.is(key);
        }

        public Set<ResourceKey<T>> getValues(HolderLookup.RegistryLookup<T> lookup) {
            return key != null ? Set.of(key) : lookup.get(tag).stream().flatMap(a -> a.stream()).map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        }
    }
}
