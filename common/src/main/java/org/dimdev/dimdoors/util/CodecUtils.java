package org.dimdev.dimdoors.util;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import org.dimdev.limlib.api.util.Path;
import org.dimdev.limlib.api.util.ResourceUtil;
import org.dimdev.dimdoors.world.decay.conditions.GenericDecayCondition;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CodecUtils {
    private static Music createMusic(Holder<SoundEvent> sound) {
        return new Music(sound, 0, 0, true);
    }

    public static final Codec<Music> GAME_MUSIC = Codec.withAlternative(Music.CODEC, SoundEvent.CODEC, CodecUtils::createMusic);

    public static final Codec<Integer> STRING_INT = Codec.STRING.xmap(Integer::valueOf, value -> Integer.toString(value));

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

//        return Codec.PASSTHROUGH.flatXmap(new Function<Dynamic<?>, DataResult<? extends T>>() {
//            @Override
//            public DataResult<? extends T> apply(Dynamic<?> dynamic) {
//                var optional = dynamic.asString().flatMap(ResourceLocation::read).map(a -> a.withSuffix(".json").withPrefix(path)).flatMap(resourceLocation -> {
//                    return ResourceUtil.loadResource(manager, resourceLocation, ResourceUtil.JSON_READER.andThenComposable(json -> JsonOps.INSTANCE.withParser(base).apply(json)));
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
                        var locationResult = ResourceLocation.read(stringResult.get()).resultOrPartial(a -> System.out.println("Error location not found: " + a));
                        if (locationResult.isPresent()) {
                            var resourceLocation = locationResult.get().withSuffix(".json").withPrefix(path);
                            var loaded = ResourceUtil.loadResource(manager, resourceLocation, ResourceUtil.JSON_READER.andThenComposable(json -> JsonOps.INSTANCE.withParser(base).apply(json).ifError(a -> System.out.println("Error with " + resourceLocation + ": " + a.message()))));
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

    public static final Codec<Path<String>> RESOURECE_LOCATION_PATH_CODEC = ResourceLocation.CODEC.flatXmap(a -> DataResult.success(Path.stringPath(a)), a -> DataResult.error(() -> " can not encode path."));

    public static <T> Codec<T> codecWithMapFallback(Codec<T> base, Function<Path<String>, T> function) {
        return Codec.withAlternative(base, RESOURECE_LOCATION_PATH_CODEC, function);

/*
        return Codec.PASSTHROUGH.flatXmap(new Function<Dynamic<?>, DataResult<T>>() {
            @Override
            public DataResult<T> apply(Dynamic<?> dynamic) {
                var stringResult = codecPath.parse(dynamic);

                if(stringResult.isSuccess()) {
                    return stringResult;
                } else {

                    return base.parse(dynamic);
                }
            }
        }, new Function<T, DataResult<Dynamic<?>>>() {
            @Override
            public DataResult<Dynamic<?>> apply(T t) {
                return DataResult.error(() -> "");
            }
        });*/
    }

    public static <T> Codec<Map<T, CompoundTag>> createTagMapCodec(Codec<T> codec) {
        var mapCodec = codec.fieldOf("Pos");
        return CompoundTag.CODEC.flatXmap(nbt -> {
            if (nbt.contains("Id") && !nbt.contains("id")) {
                nbt.putString("id", nbt.getString("Id"));
            }

            if(!nbt.contains("id")) {
                return DataResult.error(() -> "The tag did not have an 'id' nbt string");
            }

            return DataResult.success(nbt);
        }, DataResult::success).flatXmap(tagToPair(mapCodec), pairToTag(mapCodec)).listOf().xmap(entries -> entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), map -> map.entrySet().stream().toList());

    }

    private static <T> Function<CompoundTag, DataResult<Map.Entry<T, CompoundTag>>> tagToPair(MapCodec<T> codec) {
        return tag -> codec.compressedDecode(NbtOps.INSTANCE, tag).map(t -> Map.entry(t, tag));
    }

    private static <T> Function<Map.Entry<T, CompoundTag>, DataResult<CompoundTag>> pairToTag(MapCodec<T> codec) {
        return pair -> codec.encode(pair.getKey(), NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder()).build(pair.getValue()).map(CompoundTag.class::cast);
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
            return Codec.STRING.comapFlatMap(string -> string.startsWith("#") ? ResourceLocation.read(string.substring(1)).map(resourceLocation -> new TagOrElementLocation<>(resourceLocation, true, key)) : ResourceLocation.read(string).map(resourceLocation -> new TagOrElementLocation<T>(resourceLocation, false, key)), TagOrElementLocation::decoratedId);
        }

        public static <T> TagOrElementLocation<T> of(TagKey<T> tag, ResourceKey<Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.location(), true, registry);
        }

        public static <T> TagOrElementLocation<T> of(ResourceKey<T> tag, ResourceKey<Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.location(), false, registry);
        }

        public TagOrElementLocation(ResourceLocation id, boolean tag, ResourceKey<Registry<T>> registryResourceKey) {
            if(tag) this.tag = TagKey.create(registryResourceKey, id);
            else this.key = ResourceKey.create(registryResourceKey, id);
        }

        @Override
            public String toString() {
                return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag != null ? "#" + tag.location() : this.key.location().toString();
        }

        public boolean test(Holder<T> holder) {
            return tag != null && holder.is(tag) || holder.is(key);
        }

        public Set<ResourceKey<T>> getValues(HolderLookup.RegistryLookup<T> lookup) {
            return key != null ? Set.of(key) : lookup.get(tag).stream().flatMap(a -> a.stream()).map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        }
    }
}
