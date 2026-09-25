package org.dimdev.dimcore.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import org.joml.Matrix4f

interface IDimensionSpecialEffectExtension {
    fun extRenderWeather(level: ClientLevel, ticks: Int, partialTick: Float, lightTexture: LightTexture, camX: Double, camY: Double, camZ: Double): Boolean = false
    fun extTickRain(level: ClientLevel, ticks: Int, camera: Camera): Boolean = false
    fun extRenderSky(level: ClientLevel, ticks: Int, partialTick: Float, frustumMatrix: Matrix4f, camera: Camera, projectionMatrix: Matrix4f, isFoggy: Boolean, skyFogSetup: Runnable): Boolean = false
    fun extRenderClouds(level: ClientLevel, ticks: Int, partialTick: Float, poseStack: PoseStack, camX: Double, camY: Double, camZ: Double, modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f): Boolean = false
}
