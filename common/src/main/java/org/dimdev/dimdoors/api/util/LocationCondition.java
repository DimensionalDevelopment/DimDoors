package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
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

        public static final Codec<LocationCondition> CODEC = Codec.unit(INSTANCE);

        @Override
        public boolean test(Location location) {
            return true;
        }

        @Override
        public LocationConditionType<LocationCondition> type() {
            return LocationConditionType.ALWAYS_TRUE.get();
        }
    }

    public record LocationConditionType<T extends LocationCondition>(Codec<T> codec) {
        public static final Registrar<LocationConditionType<? extends LocationCondition>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<LocationConditionType<? extends LocationCondition>>builder(DimensionalDoors.id("location_condition_type")).build();


        public static final Codec<LocationConditionType<? extends LocationCondition>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

        public static final RegistrySupplier<LocationConditionType<LocationCondition>> ALWAYS_TRUE = register(DimensionalDoors.id("always_true"), AlwaysTrue.CODEC);

        public static void register() {
        }

        static <T, V, U extends LocationCondition> RegistrySupplier<LocationConditionType<U>> register(ResourceLocation id, Codec<U> codec) {
            return REGISTRY.register(id, () -> new LocationConditionType<>(codec));
        }
    }
}
