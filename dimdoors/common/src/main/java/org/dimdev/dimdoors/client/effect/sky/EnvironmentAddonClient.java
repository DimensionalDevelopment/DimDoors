package org.dimdev.dimdoors.client.effect.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.CloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.*;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.SkyData.SkyDataType;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.WeatherData.WeatherDataType;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentAddonClient {
    private static final Map<CloudData.CloudDataType<?>, CloudRenderer<?>> CLOUD_RENDERERS = new HashMap<>();
    private static final Map<SkyDataType<?>, SkyRenderer<?>> SKY_RENDERERS = new HashMap<>();
    private static final Map<WeatherDataType<?>, WeatherRenderer<?>> WEATHER_RENDERERS = new HashMap<>();

    public static <T extends CloudData> void registerCloudRendrer(CloudData.CloudDataType<T> type, CloudRenderer<T> rendrer) {
        CLOUD_RENDERERS.put(type, rendrer);
    }

    public static <T extends SkyData> void registerSkyRendrer(SkyDataType<T> type, SkyRenderer<T> rendrer) {
        SKY_RENDERERS.put(type, rendrer);
    }

    public static <T extends WeatherData> void registerWeatherRendrer(WeatherDataType<T> type, WeatherRenderer<T> rendrer) {
        WEATHER_RENDERERS.put(type, rendrer);
    }

    public static <T extends WeatherData> void renderWeather(T data, ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        WeatherRenderer<T> renderer = (WeatherRenderer<T>) WEATHER_RENDERERS.get(data.type());

        if(renderer != null) renderer.render(data, level, ticks, partialTick, lightTexture, camX, camY, camZ);
    }

    public static <T extends CloudData> void renderCloud(T data, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        CloudRenderer<T> renderer = (CloudRenderer<T>) CLOUD_RENDERERS.get(data.type());

        if(renderer != null) renderer.render(data, level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix);
    }

    public static <T extends SkyData> void renderSky(T data, ClientLevel level, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, boolean isFoggy, Runnable fogSetup, Camera camera) {
        var renderer = (SkyRenderer<T>) SKY_RENDERERS.get(data.type());

        if(renderer != null) {
            renderer.render(data, level, poseStack, projectionMatrix, partialTick, isFoggy, fogSetup, camera);
        }
    }

    interface CloudRenderer<T extends CloudData> {
        void render(T data, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix);
    }

    interface SkyRenderer<T extends SkyData> {
        void render(T data, ClientLevel level, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, boolean isFoggy, Runnable skyFogSetup, Camera camera);
    }

    interface WeatherRenderer<T extends WeatherData> {
        void render(T data, ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ);
    }

    public static void init() {
        registerSkyRendrer(SkyDataType.OVERWORLD, OverworldEnvironmentRendering::renderSky);
        registerCloudRendrer(CloudData.CloudDataType.OVERWORLD, OverworldEnvironmentRendering::renderCloud);
        registerWeatherRendrer(WeatherDataType.OVERWORLD, OverworldEnvironmentRendering::renderWeather);
        registerSkyRendrer(SkyDataType.END, EndEnvironmentRendering::renderSky);
    }
}
