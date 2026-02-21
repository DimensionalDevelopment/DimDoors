package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import net.minecraft.world.level.biome.Biome;

public interface OverworldWeatherData extends WeatherData {
    @Override
    default WeatherData.WeatherDataType<?> type() {
        return WeatherData.WeatherDataType.OVERWORLD;
    }

    Biome.Precipitation getPrecepitation();

    float getRainLevel();
}
