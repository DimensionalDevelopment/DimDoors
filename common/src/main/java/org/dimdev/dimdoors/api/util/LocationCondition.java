package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.List;
import java.util.function.Function;

public interface LocationCondition {
    Codec<LocationCondition> CODEC = LocationConditionType.CODEC.dispatch("type", LocationCondition::type, LocationConditionType::codec);
    Codec<List<LocationCondition>> LIST_CODEC = Codec.either(CODEC, CODEC.listOf()).xmap(either -> either.map(List::of, Function.identity()), conditions -> conditions.size() > 1 ? Either.right(conditions) : Either.left(conditions.get(0)));

    public boolean test(Location location);
    LocationConditionType<? extends LocationCondition> type();

    public enum AlwaysTrue implements LocationCondition {
        INSTANCE;

        public static final MapCodec<AlwaysTrue> CODEC = MapCodec.unit(INSTANCE).stable();

        @Override
        public boolean test(Location location) {
            return true;
        }

        @Override
        public LocationConditionType<AlwaysTrue> type() {
            return LocationConditionType.ALWAYS_TRUE;
        }
    }

    public record LocationConditionType<T extends LocationCondition>(MapCodec<T> codec) {
        public static final ResourceKey<Registry<LocationConditionType<? extends LocationCondition>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("location_condition_type"));
        public static final Registry<LocationConditionType<? extends LocationCondition>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);


        public static final Codec<LocationConditionType<? extends LocationCondition>> CODEC = REGISTRY.byNameCodec();

        public static final LocationConditionType<AlwaysTrue> ALWAYS_TRUE = register(DimensionalDoors.id("always_true"), AlwaysTrue.CODEC);

        public static void register() {
        }

        static <U extends LocationCondition> LocationConditionType<U> register(ResourceLocation id, MapCodec<U> codec) {
            return DimensionalDoors.getSided().register(KEY, id, new LocationConditionType<>(codec));
        }
    }
}
