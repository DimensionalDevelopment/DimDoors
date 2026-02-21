package org.dimdev.dimdoors.world.pocket.type.addon.sky;

public enum EmptyWeatherData implements WeatherData {
    INSTANCE;

    @Override
    public WeatherDataType<?> type() {
        return WeatherDataType.EMPTY;
    }
}
