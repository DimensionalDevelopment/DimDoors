package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudDataImpl;

import java.util.HashMap;
import java.util.Map;

public interface WeatherData {
    Codec<WeatherData> CODEC = WeatherDataType.CODEC.dispatch(WeatherData::type, WeatherDataType::codec);
    StreamCodec<RegistryFriendlyByteBuf, WeatherData> STREAM_CODEC = WeatherDataType.STREAM_CODEC.dispatch(WeatherData::type, WeatherDataType::streamCodec);


    static WeatherData empty() {
        return EmptyWeatherData.INSTANCE;
    }

    WeatherDataType<?> type();

    public record WeatherDataType<T extends WeatherData>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {

        private static final Map<Identifier, WeatherDataType<?>> ID_TO_TYPE = new HashMap<>();
        private static final Map<WeatherDataType<?>, Identifier> TYPE_TO_ID = new HashMap<>();

        public static Codec<WeatherDataType<?>> CODEC = Identifier.CODEC.xmap(ID_TO_TYPE::get, TYPE_TO_ID::get);
        public static StreamCodec<RegistryFriendlyByteBuf, WeatherDataType<?>> STREAM_CODEC = Identifier.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(ID_TO_TYPE::get, TYPE_TO_ID::get);

        public static final WeatherDataType<EmptyWeatherData> EMPTY = register(DimensionalDoors.id("empty"), MapCodec.unit(EmptyWeatherData.INSTANCE), StreamCodec.unit(EmptyWeatherData.INSTANCE));
        public static final WeatherDataType<OverworldWeatherData> OVERWORLD = register(DimensionalDoors.id("overworld"), OverworldWeatherDataImpl.CODEC, OverworldWeatherDataImpl.STREAM_CODEC);

        public static <U extends WeatherData> WeatherDataType<U> register(Identifier id, MapCodec<U> codec, StreamCodec<RegistryFriendlyByteBuf, U> streamCodec) {
            var type = new WeatherDataType<>(codec, streamCodec);
            ID_TO_TYPE.put(id, type);
            TYPE_TO_ID.put(type, id);
            return type;
        }
    }
}
