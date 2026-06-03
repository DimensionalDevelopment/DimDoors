package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.EmptyCloudData;

import java.util.Map;

public enum EmptyEnvironment implements Environment {
    INSTANCE;

    public static MapCodec<EmptyEnvironment> CODEC = MapCodec.unit(INSTANCE);
    public static StreamCodec<RegistryFriendlyByteBuf, EmptyEnvironment> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public SkyData sky() {
        return SkyData.empty();
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
        return EnvironmentType.EMPTY;
    }
}
