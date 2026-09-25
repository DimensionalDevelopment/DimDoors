package org.dimdev.dimdoors.client.effect.sky

import com.mojang.blaze3d.vertex.PoseStack
import me.shedaniel.rei.impl.common.util.Weather
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.Holder
import org.dimdev.dimcore.api.Type
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.OverworldCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.EndSkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.OverWorldSkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.OverworldWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData
import org.joml.Matrix4f


object EnvironmentAddonClient {
    private val CLOUD_RENDERERS = mutableMapOf<Holder<out Type<CloudData>>, CloudRenderer<out CloudData>>()
    private val SKY_RENDERERS = mutableMapOf<Holder<out Type<SkyData>>, SkyRenderer<out SkyData>>()
    private val WEATHER_RENDERERS = mutableMapOf<Holder<out Type<WeatherData>>, WeatherRenderer<out WeatherData>>()

    fun <T : CloudData> registerCloudRendrer(type: Holder<out Type<T>>, rendrer: CloudRenderer<T>) {
        CLOUD_RENDERERS[type] = rendrer
    }

    fun <T : SkyData> registerSkyRendrer(type: Holder<out Type<T>>, rendrer: SkyRenderer<T>) {
        SKY_RENDERERS[type] = rendrer
    }

    fun <T : WeatherData> registerWeatherRendrer(type: Holder<out Type<T>>, rendrer: WeatherRenderer<T>) {
        WEATHER_RENDERERS[type] = rendrer
    }

    private fun <T : CloudData> getCloudRenderer(data: T): CloudRenderer<T>? = CLOUD_RENDERERS[data.type] as CloudRenderer<T>?

    private fun <T : SkyData> getSkyRenderer(data: T): SkyRenderer<T>? = SKY_RENDERERS[data.type] as SkyRenderer<T>?

    private fun <T : WeatherData> getWeatherRenderer(data: T): WeatherRenderer<T>? = WEATHER_RENDERERS[data.type] as WeatherRenderer<T>?

    fun <T : WeatherData> renderWeather(data: T, level: ClientLevel, ticks: Int, partialTick: Float, lightTexture: LightTexture, camX: Double, camY: Double, camZ: Double) = getWeatherRenderer(data)?.render(data, level, ticks, partialTick, lightTexture, camX, camY, camZ)

    fun <T : CloudData> renderCloud(data: T, level: ClientLevel, ticks: Int, partialTick: Float, poseStack: PoseStack, camX: Double, camY: Double, camZ: Double, modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f) = getCloudRenderer(data)?.render(data, level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix)

    fun <T : SkyData> renderSky(data: T, level: ClientLevel, poseStack: PoseStack, projectionMatrix: Matrix4f, partialTick: Float, isFoggy: Boolean, fogSetup: Runnable, camera: Camera) = getSkyRenderer(data)?.render(data, level, poseStack, projectionMatrix, partialTick, isFoggy, fogSetup, camera)

    fun init() {
        registerSkyRendrer(SkyDatum.OVERWORLD, OverworldEnvironmentRendering::renderSky)
        registerCloudRendrer(CloudDatum.OVERWORLD, OverworldEnvironmentRendering::renderCloud)
        registerWeatherRendrer(
            WeatherDatum.OVERWORLD,
            EnvironmentAddonClient.WeatherRenderer { data: T?, level: ClientLevel?, ticks: Int, partialTick: Float, lightTexture: LightTexture?, camX: Double, camY: Double, camZ: Double ->
                OverworldEnvironmentRendering.renderWeather(
                    data,
                    level,
                    ticks,
                    partialTick,
                    lightTexture,
                    camX,
                    camY,
                    camZ
                )
            })
        registerSkyRendrer(SkyDatum.END, EndEnvironmentRendering::renderSky)
    }

    fun interface CloudRenderer<T : CloudData> {
        fun render(data: T, level: ClientLevel, ticks: Int, partialTick: Float, poseStack: PoseStack, camX: Double, camY: Double, camZ: Double, modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f)
    }

    fun interface SkyRenderer<T : SkyData> {
        fun render(data: T, level: ClientLevel, poseStack: PoseStack, projectionMatrix: Matrix4f, partialTick: Float, isFoggy: Boolean, skyFogSetup: Runnable, camera: Camera)
    }

    fun interface WeatherRenderer<T : WeatherData> {
        fun render(data: T, level: ClientLevel, ticks: Int, partialTick: Float, lightTexture: LightTexture, camX: Double, camY: Double, camZ: Double)
    }
}
