package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;

public class EnvironmentAddon implements PocketAddon {
    public static ResourceLocation ID = DimensionalDoors.id("environment");
    public static final MapCodec<EnvironmentAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Environment.CODEC.optionalFieldOf("environment", EmptyEnvironment.INSTANCE).forGetter(a -> a.environment)).apply(instance, EnvironmentAddon::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnvironmentAddon> STREAM_CODEC = StreamCodec.composite(Environment.STREAM_CODEC, a -> a.environment, EnvironmentAddon::new);

    private Environment environment = EmptyEnvironment.INSTANCE;

    public EnvironmentAddon() {
        this(EmptyEnvironment.INSTANCE);
    }

    public EnvironmentAddon(Environment environment) {
        this.environment = environment;
    }

    @Override
    public PocketAddonType<?, ?> getType() {
    return PocketAddonType.ENVIRONMENT_ADDON.get();
    }

    public SkyData getSky() {
        return environment.getSky();
    }

    public CloudData getCloud() {
        return environment.getCloud();
    }

    public WeatherData getWeather() {
        return environment.getWeather();
    }

    public static class EnvironmentBuilderAddon implements PocketBuilderAddon<EnvironmentAddon, EnvironmentBuilderAddon> {
        public static final MapCodec<EnvironmentBuilderAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Environment.CODEC.optionalFieldOf("environment", EmptyEnvironment.INSTANCE).forGetter(a -> a.environment)).apply(instance, EnvironmentBuilderAddon::new));

        private Environment environment = EmptyEnvironment.INSTANCE;

        public EnvironmentBuilderAddon(Environment environment) {
            this.environment = environment;
        }

    @Override
    public void apply(Pocket pocket) {
        EnvironmentAddon addon = new EnvironmentAddon(environment);
        pocket.addAddon(addon);
    }

    @Override
    public PocketAddonType<EnvironmentAddon, EnvironmentBuilderAddon> getType() {
        return PocketAddonType.ENVIRONMENT_ADDON.get();
    }
    }

}