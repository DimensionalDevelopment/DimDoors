package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.List;

public interface LocationValue {
    MapCodec<LocationValue> CODEC = Codec.mapEither(Constant.CODEC, LocationValueWithType.TYPE_CODEC).xmap(either -> either.map(constant -> constant, locationValueWithType -> locationValueWithType), value -> value instanceof Constant constant ? Either.left(constant) : Either.right((LocationValueWithType) value));

    float value(Location location, RandomSource source);

    interface LocationValueWithType extends LocationValue {
        MapCodec<LocationValueWithType> TYPE_CODEC = LocationValueType.CODEC.dispatchMap("type", LocationValueWithType::type, LocationValueType::codec);

        static void register() {

        }

        LocationValueType<? extends LocationValueWithType> type();
    }

    record Complex(List<LocationCondition> conditions, FloatProvider value, FloatProvider fallback) implements LocationValueWithType {
        public static final MapCodec<Complex> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(LocationCondition.LIST_CODEC.fieldOf("conditions").forGetter(Complex::conditions), FloatProvider.CODEC.fieldOf("value").forGetter(Complex::value), FloatProvider.CODEC.fieldOf("fallback").forGetter(Complex::value)).apply(instance, Complex::new));

        @Override
        public float value(Location location, RandomSource source) {
            return (conditions.stream().allMatch(a -> a.test(location)) ? value : fallback).sample(source);
        }

        @Override
        public LocationValueType<? extends LocationValueWithType> type() {
            return LocationValueType.COMPLEX;
        }
    }

    record Simple(FloatProvider value) implements LocationValueWithType {
        public static final MapCodec<Simple> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(FloatProvider.CODEC.fieldOf("value").forGetter(Simple::value)).apply(instance, Simple::new));
        @Override
        public float value(Location location, RandomSource source) {
            return value.sample(source);
        }

        @Override
        public LocationValueType<? extends LocationValueWithType> type() {
            return LocationValueType.SIMPLE;
        }
    }

    record Constant(float value) implements LocationValue {
        public static final MapCodec<Constant> CODEC = MapCodec.assumeMapUnsafe(Codec.FLOAT.xmap(Constant::new, Constant::value));
        public static final Constant ZERO = new Constant(0);

        @Override
        public float value(Location location, RandomSource source) {
            return value;
        }
    }

    record LocationValueType<T extends LocationValueWithType>(MapCodec<T> codec) {
        public static final ResourceKey<Registry<LocationValueType<? extends LocationValue>>> KEY = ResourceKey.<LocationValueType<? extends LocationValue>>createRegistryKey(DimensionalDoors.id("location_value_type"));

        public static final Registry<LocationValueType<? extends LocationValue>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);

        public static final Codec<LocationValueType<? extends LocationValue>> CODEC = REGISTRY.byNameCodec();

        public static final LocationValueType<Simple> SIMPLE = register("simple", Simple.CODEC);
        public static final LocationValueType<Complex> COMPLEX = register("complex", Complex.CODEC);

        public static void register() {
        }

        static <T, V, U extends LocationValueWithType> LocationValueType<U> register(String id, MapCodec<U> codec) {
            return DimensionalDoors.getSided().register(KEY, id, new LocationValueType<>(codec));
        }
    }
}