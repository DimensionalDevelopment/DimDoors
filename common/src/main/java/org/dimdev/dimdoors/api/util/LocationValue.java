package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.List;

public interface LocationValue {
    public Codec<LocationValue> CODEC = Codec.either(Constant.CODEC, LocationValueWithType.TYPE_CODEC).xmap(either -> either.map(constant -> constant, locationValueWithType -> locationValueWithType), value -> value instanceof Constant constant ? Either.left(constant) : Either.right((LocationValueWithType) value));

    public float value(Location location, RandomSource source);

    public interface LocationValueWithType extends LocationValue {
        Codec<LocationValueWithType> TYPE_CODEC = LocationValueType.CODEC.dispatch("type", LocationValueWithType::type, LocationValueType::codec);

        static void register() {

        }

        LocationValueType<? extends LocationValueWithType> type();
    }

    public record Complex(List<LocationCondition> conditions, FloatProvider value, FloatProvider fallback) implements LocationValueWithType {
        public static final Codec<Complex> CODEC = RecordCodecBuilder.create(instance -> instance.group(LocationCondition.LIST_CODEC.fieldOf("conditions").forGetter(Complex::conditions), FloatProvider.CODEC.fieldOf("value").forGetter(Complex::value), FloatProvider.CODEC.fieldOf("fallback").forGetter(Complex::value)).apply(instance, Complex::new));

        @Override
        public float value(Location location, RandomSource source) {
            return (conditions.stream().allMatch(a -> a.test(location)) ? value : fallback).sample(source);
        }

        @Override
        public LocationValueType<? extends LocationValueWithType> type() {
            return LocationValueType.COMPLEX.get();
        }
    }

    public record Simple(FloatProvider value) implements LocationValueWithType {
        public static final Codec<Simple> CODEC = RecordCodecBuilder.create(instance -> instance.group(FloatProvider.CODEC.fieldOf("value").forGetter(Simple::value)).apply(instance, Simple::new));
        @Override
        public float value(Location location, RandomSource source) {
            return value.sample(source);
        }

        @Override
        public LocationValueType<? extends LocationValueWithType> type() {
            return LocationValueType.SIMPLE.get();
        }
    }

    public record Constant(float value) implements LocationValue {
        public static final Codec<Constant> CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
        public static final Constant ZERO = new Constant(0);

        @Override
        public float value(Location location, RandomSource source) {
            return value;
        }
    }

    public record LocationValueType<T extends LocationValueWithType>(Codec<T> codec) {
        public static final Registrar<LocationValueType<? extends LocationValue>> REGISTRY = Registries.get(DimensionalDoors.MOD_ID).<LocationValueType<? extends LocationValue>>builder(DimensionalDoors.id("location_value_type")).build();

        public static final Codec<LocationValueType<? extends LocationValue>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

        public static final RegistrySupplier<LocationValueType<Simple>> SIMPLE = register(DimensionalDoors.id("simple"), Simple.CODEC);
        public static final RegistrySupplier<LocationValueType<Complex>> COMPLEX = register(DimensionalDoors.id("complex"), Complex.CODEC);

        public static void register() {
        }

        static <T, V, U extends LocationValueWithType> RegistrySupplier<LocationValueType<U>> register(ResourceLocation id, Codec<U> codec) {
            return REGISTRY.register(id, () -> new LocationValueType<>(codec));
        }
    }
}