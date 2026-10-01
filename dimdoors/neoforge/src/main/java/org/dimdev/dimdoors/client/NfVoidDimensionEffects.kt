package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import org.dimdev.dimdoors.client.effect.DimensionEffect
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects
import org.joml.Matrix4f

class NfVoidDimensionEffects(private val renderer: DimensionEffect) : VoidDimensionSpecialEffects() {
    override fun renderSky(level: ClientLevel, ticks: Int, partialTick: Float, modelViewMatrix: Matrix4f, camera: Camera, projectionMatrix: Matrix4f, isFoggy: Boolean, setupFog: Runnable) = renderer.renderSky(level, ticks, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog)

    override fun renderClouds(level: ClientLevel, ticks: Int, partialTick: Float, poseStack: PoseStack, camX: Double, camY: Double, camZ: Double, modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f) = renderer.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix)

    override fun renderSnowAndRain(level: ClientLevel, ticks: Int, partialTick: Float, lightTexture: LightTexture, camX: Double, camY: Double, camZ: Double) = renderer.renderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ)
}
