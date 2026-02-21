package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

public record OverworldWeatherDataImpl(Biome.Precipitation precipitation, float rainLevel) implements OverworldWeatherData {
    public static final MapCodec<OverworldWeatherData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.Precipitation.CODEC.optionalFieldOf("precipitation", Biome.Precipitation.RAIN).forGetter(OverworldWeatherData::getPrecepitation),
            Codec.floatRange(0f, 1f).optionalFieldOf("rainLevel", 0f).forGetter(OverworldWeatherData::getRainLevel)
    ).apply(instance, OverworldWeatherDataImpl::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OverworldWeatherData> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, OverworldWeatherData>() {
        @Override
        public OverworldWeatherDataImpl decode(RegistryFriendlyByteBuf buf) {
            return new OverworldWeatherDataImpl(buf.readEnum(Biome.Precipitation.class), buf.readFloat());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OverworldWeatherData data) {
            buf.writeEnum(data.getPrecepitation());
            buf.writeFloat(data.getRainLevel());
        }
    };

    @Override
    public Biome.Precipitation getPrecepitation() {
        return precipitation;
    }

    @Override
    public float getRainLevel() {
        return rainLevel;
    }
}
