package org.dimdev.dimdoors.world.pocket.type.addon.cloud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.WeatherData;

import java.util.HashMap;
import java.util.Map;

public interface CloudData {
    Codec<CloudData> CODEC = CloudDataType.CODEC.dispatch(CloudData::type, CloudDataType::codec);
    StreamCodec<RegistryFriendlyByteBuf, CloudData> STREAM_CODEC = CloudDataType.STREAM_CODEC.dispatch(CloudData::type, CloudDataType::streamCodec);

    static CloudData empty() {
        return EmptyCloudData.INSTANCE;
    }

    CloudDataType<?> type();

    public record CloudDataType<T extends CloudData>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        private static final Map<ResourceLocation, CloudDataType<?>> ID_TO_TYPE = new HashMap<>();
        private static final Map<CloudDataType<?>, ResourceLocation> TYPE_TO_ID = new HashMap<>();

        public static Codec<CloudDataType<?>> CODEC = ResourceLocation.CODEC.xmap(ID_TO_TYPE::get, TYPE_TO_ID::get);
        public static StreamCodec<RegistryFriendlyByteBuf, CloudDataType<?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(ID_TO_TYPE::get, TYPE_TO_ID::get);

        public static final CloudDataType<EmptyCloudData> EMPTY = register(DimensionalDoors.id("empty"), MapCodec.unit(EmptyCloudData.INSTANCE), StreamCodec.unit(EmptyCloudData.INSTANCE));
        public static final CloudDataType<OverworldCloudData> OVERWORLD = register(DimensionalDoors.id("overworld"), OverworldCloudDataImpl.CODEC, OverworldCloudDataImpl.STREAM_CODEC);

        public static <U extends CloudData> CloudDataType<U> register(ResourceLocation id, MapCodec<U> codec, StreamCodec<RegistryFriendlyByteBuf, U> streamCodec) {
            var type = new CloudDataType<>(codec, streamCodec);

            ID_TO_TYPE.put(id, type);
            TYPE_TO_ID.put(type, id);

            return type;
        }
    }
}
