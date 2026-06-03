package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface SkyData {
    Codec<SkyData> CODEC = SkyDataType.CODEC.dispatch(SkyData::type, SkyDataType::codec);
    StreamCodec<RegistryFriendlyByteBuf, SkyData> STREAM_CODEC = SkyDataType.STREAM_CODEC.dispatch(SkyData::type, SkyDataType::streamCodec);

    static SkyData empty() {
        return EmptySkyData.INSTANCE;
    }

    SkyDataType<?> type();


    public record SkyDataType<T extends SkyData>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {

        private static final Map<Identifier, SkyDataType<?>> ID_TO_TYPE = new HashMap<>();
        private static final Map<SkyDataType<?>, Identifier> TYPE_TO_ID = new HashMap<>();

        public static Codec<SkyDataType<?>> CODEC = Identifier.CODEC.xmap(ID_TO_TYPE::get, TYPE_TO_ID::get);
        public static StreamCodec<RegistryFriendlyByteBuf, SkyDataType<?>> STREAM_CODEC = Identifier.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(ID_TO_TYPE::get, TYPE_TO_ID::get);

        public static final SkyDataType<EmptySkyData> EMPTY = register(DimensionalDoors.id("empty"), MapCodec.unit(EmptySkyData.INSTANCE), StreamCodec.unit(EmptySkyData.INSTANCE));
        public static final SkyDataType<EndSkyData> END = register(DimensionalDoors.id("end"), MapCodec.unit(EndSkyData.INSTANCE), StreamCodec.unit(EndSkyData.INSTANCE));
        public static final SkyDataType<OverWorldSkyData> OVERWORLD = register(DimensionalDoors.id("overworld"), OverWorldSkyDataImpl.CODEC, OverWorldSkyDataImpl.STREAM_CODEC);

        public static <U extends SkyData> SkyDataType<U> register(Identifier id, MapCodec<U> codec, StreamCodec<RegistryFriendlyByteBuf, U> streamCodec) {
            var type = new SkyDataType<>(codec, streamCodec);

            ID_TO_TYPE.put(id, type);
            TYPE_TO_ID.put(type, id);

            return type;
        }
    }
}
