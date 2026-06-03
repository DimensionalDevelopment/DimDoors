package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;

public record ComplexEnvironment(SkyData sky, WeatherData weather, CloudData cloud) implements Environment {
    public static final MapCodec<ComplexEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SkyData.CODEC.lenientOptionalFieldOf("sky", SkyData.empty()).forGetter(ComplexEnvironment::sky),
            WeatherData.CODEC.lenientOptionalFieldOf("weather", WeatherData.empty()).forGetter(ComplexEnvironment::weather),
            CloudData.CODEC.lenientOptionalFieldOf("cloud", CloudData.empty()).forGetter(ComplexEnvironment::cloud)
    ).apply(instance, ComplexEnvironment::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ComplexEnvironment> STREAM_CODEC = StreamCodec.composite(
            SkyData.STREAM_CODEC, ComplexEnvironment::sky,
            WeatherData.STREAM_CODEC, ComplexEnvironment::weather,
            CloudData.STREAM_CODEC, ComplexEnvironment::cloud,
            ComplexEnvironment::new
    );

    public ComplexEnvironment() {
        this(SkyData.empty(), WeatherData.empty(), CloudData.empty());
    }

    @Override
    public EnvironmentType<?> getType() {
        return EnvironmentType.COMPLEX;
    }
}
