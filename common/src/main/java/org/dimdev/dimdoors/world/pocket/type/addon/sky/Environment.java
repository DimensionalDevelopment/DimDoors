package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface Environment {
    Codec<Environment> CODEC = EnvironmentType.CODEC.dispatch(Environment::getType, EnvironmentType::codec);
    StreamCodec<RegistryFriendlyByteBuf, Environment> STREAM_CODEC = EnvironmentType.STREAM_CODEC.dispatch(Environment::getType, EnvironmentType::streamCodec);

    SkyData getSky();

    public CloudData getCloud();

    public WeatherData getWeather();

    public EnvironmentType<?> getType();

    public record EnvironmentType<T extends Environment>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {

        private static final Map<ResourceLocation, EnvironmentType<?>> ID_TO_TYPE = new HashMap<>();
        private static final Map<EnvironmentType<?>, ResourceLocation> TYPE_TO_ID = new HashMap<>();

        public static Codec<EnvironmentType<?>> CODEC = ResourceLocation.CODEC.xmap(ID_TO_TYPE::get, TYPE_TO_ID::get);
        public static StreamCodec<RegistryFriendlyByteBuf, EnvironmentType<?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(ID_TO_TYPE::get, TYPE_TO_ID::get);

        public static final EnvironmentType<EmptyEnvironment> EMPTY = register(DimensionalDoors.id("empty"), EmptyEnvironment.CODEC, EmptyEnvironment.STREAM_CODEC);
        public static final EnvironmentType<ComplexEnvironment> COMPLEX = register(DimensionalDoors.id("complex"), ComplexEnvironment.CODEC, ComplexEnvironment.STREAM_CODEC);
        public static final EnvironmentType<EndEnvironment> END = register(DimensionalDoors.id("end"), EndEnvironment.CODEC, EndEnvironment.STREAM_CODEC);
        public static final EnvironmentType<OverworldEnvironment> OVERWORLD = register(DimensionalDoors.id("overworld"), OverworldEnvironment.CODEC, OverworldEnvironment.STREAM_CODEC);

        public static <U extends Environment> EnvironmentType<U> register(ResourceLocation id, MapCodec<U> codec, StreamCodec<RegistryFriendlyByteBuf, U> streamCodec) {
            var type = new EnvironmentType<>(codec, streamCodec);

            ID_TO_TYPE.put(id, type);
            TYPE_TO_ID.put(type, id);

            return type;
        }
    }
}
