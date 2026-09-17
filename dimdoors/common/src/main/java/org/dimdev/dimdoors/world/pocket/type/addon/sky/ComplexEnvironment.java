package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;

public class ComplexEnvironment implements Environment {
    public static final MapCodec<ComplexEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SkyData.CODEC.lenientOptionalFieldOf("sky", SkyData.empty()).forGetter(ComplexEnvironment::getSky),
            WeatherData.CODEC.lenientOptionalFieldOf("weather", WeatherData.empty()).forGetter(ComplexEnvironment::getWeather),
            CloudData.CODEC.lenientOptionalFieldOf("cloud", CloudData.empty()).forGetter(ComplexEnvironment::getCloud)
    ).apply(instance, ComplexEnvironment::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ComplexEnvironment> STREAM_CODEC = StreamCodec.composite(
            SkyData.STREAM_CODEC, ComplexEnvironment::getSky,
            WeatherData.STREAM_CODEC, ComplexEnvironment::getWeather,
            CloudData.STREAM_CODEC, ComplexEnvironment::getCloud,
            ComplexEnvironment::new
    );

    private SkyData sky;
    private WeatherData weather;
    private CloudData cloud;

    public ComplexEnvironment() {
        this(SkyData.empty(), WeatherData.empty(), CloudData.empty());
    }

    public ComplexEnvironment(SkyData sky, WeatherData weather, CloudData cloud) {
        this.sky = sky;
        this.weather = weather;
        this.cloud = cloud;
    }

    public SkyData getSky() {
        return sky;
    }

    public CloudData getCloud() {
        return cloud;
    }

    public WeatherData getWeather() {
        return weather;
    }

    @Override
    public EnvironmentType<?> getType() {
        return EnvironmentType.COMPLEX;
    }
}
