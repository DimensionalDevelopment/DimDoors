package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;
import org.jetbrains.annotations.NotNull;

public class OverworldEnvironment implements Environment {
    public static final MapCodec<OverworldEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.LONG.optionalFieldOf("day_time", 12000L).<OverworldEnvironment>forGetter(a -> a.dayTime),
            Codec.INT.optionalFieldOf("moon_phase", 0).<OverworldEnvironment>forGetter(a -> a.moonPhase),
            Vec3.CODEC.optionalFieldOf("skyColor", new Vec3(0.486f, 0.654f, 1.0f)).<OverworldEnvironment>forGetter(a -> a.skyColor),
            Codec.FLOAT.optionalFieldOf("rain_level", 0.0f).forGetter(a -> a.rainLevel),
            Biome.Precipitation.CODEC.optionalFieldOf("precipitation", Biome.Precipitation.NONE).<OverworldEnvironment>forGetter(a -> a.precipitation),
            Codec.FLOAT.optionalFieldOf("thunder_level", 0.0f).forGetter(a -> a.thunderLevel),
            Codec.FLOAT.optionalFieldOf("cloud_height", 128f).forGetter(a -> a.cloudHeight),
            Vec3.CODEC.optionalFieldOf("cloud_color", new Vec3(1, 1, 1)).forGetter(a -> a.cloudColor)
    ).apply(instance, OverworldEnvironment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OverworldEnvironment> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, OverworldEnvironment>() {
        @Override
        public @NotNull OverworldEnvironment decode(RegistryFriendlyByteBuf buf) {
            return new OverworldEnvironment(
                    buf.readVarLong(),
                    buf.readVarInt(),
                    buf.readVec3(),
                    buf.readFloat(),
                    buf.readEnum(Biome.Precipitation.class),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readVec3()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OverworldEnvironment data) {
            buf.writeVarLong(data.dayTime);
            buf.writeVarInt(data.moonPhase);
            buf.writeVec3(data.skyColor);
            buf.writeFloat(data.rainLevel);
            buf.writeEnum(data.precipitation);
            buf.writeFloat(data.thunderLevel);
            buf.writeFloat(data.cloudHeight);
            buf.writeVec3(data.cloudColor);
        }
    };

    private final long dayTime;
    private final int moonPhase;
    private final float[] sunriseColors;
    private final Vec3 skyColor;
    private final Biome.Precipitation precipitation;
    private final float rainLevel;
    private final float thunderLevel;
    private final float cloudHeight;
    private final Vec3 cloudColor;
    private final OverworldSkyDataImpl skyData = new OverworldSkyDataImpl();
    private final OverworldCloudDataImpl cloudData = new OverworldCloudDataImpl();
    private final OverworldWeatherDataImpl weatherData = new OverworldWeatherDataImpl();

    public OverworldEnvironment(long dayTime, int moonPhase, Vec3 skyColor, float rainLevel, Biome.Precipitation precipitation, float thunderLevel, float cloudHeight, Vec3 cloudColor) {
        this.dayTime = dayTime;
        this.moonPhase = moonPhase;
        this.skyColor = skyColor;
        this.rainLevel = rainLevel;
        this.precipitation = precipitation;
        this.thunderLevel = thunderLevel;
        this.cloudHeight = cloudHeight;
        this.cloudColor = cloudColor;
        this.sunriseColors = new float[4];
    }

    @Override
    public SkyData sky() {
        return skyData;
    }

    @Override
    public CloudData cloud() {
        return cloudData;
    }

    @Override
    public WeatherData weather() {
        return weatherData;
    }

    @Override
    public EnvironmentType<?> getType() {
        return EnvironmentType.OVERWORLD;
    }

    private class OverworldSkyDataImpl implements OverWorldSkyData {

        @Override
        public long getDayTime() {
            return OverworldEnvironment.this.dayTime;
        }

        @Override
        public int getMoonPhase() {
            return OverworldEnvironment.this.moonPhase;
        }

        @Override
        public float[] getSunriseColors() {
            return OverworldEnvironment.this.sunriseColors;
        }

        @Override
        public Vec3 getSkyColor() {
            return OverworldEnvironment.this.skyColor;
        }

        @Override
        public float getRainLevel() {
            return OverworldEnvironment.this.rainLevel;
        }

        @Override
        public float getThunderLevel() {
            return OverworldEnvironment.this.thunderLevel;
        }
    }

    private class OverworldCloudDataImpl implements OverworldCloudData {

        @Override
        public float getCloudHeight() {
            return OverworldEnvironment.this.cloudHeight;
        }

        @Override
        public Vec3 getCloudColor() {
            return OverworldEnvironment.this.cloudColor;
        }
    }

    private class OverworldWeatherDataImpl implements OverworldWeatherData {

        @Override
        public Biome.Precipitation getPrecepitation() {
            return OverworldEnvironment.this.precipitation;
        }

        @Override
        public float getRainLevel() {
            return OverworldEnvironment.this.rainLevel;
        }
    }
}
