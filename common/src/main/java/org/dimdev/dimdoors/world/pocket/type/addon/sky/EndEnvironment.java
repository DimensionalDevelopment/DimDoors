package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;

public enum EndEnvironment implements Environment {
    INSTANCE;

    public static MapCodec<EndEnvironment> CODEC = MapCodec.unit(INSTANCE);
    public static StreamCodec<RegistryFriendlyByteBuf, EndEnvironment> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public SkyData sky() {
        return EndSkyData.INSTANCE;
    }

    @Override
    public CloudData cloud() {
        return CloudData.empty();
    }

    @Override
    public WeatherData weather() {
        return WeatherData.empty();
    }

    @Override
    public EnvironmentType<?> getType() {
        return EnvironmentType.END;
    }
}

