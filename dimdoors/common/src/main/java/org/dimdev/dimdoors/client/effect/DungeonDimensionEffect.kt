package org.dimdev.dimdoors.client.effect

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.BlockPos
import net.minecraft.world.level.material.FogType
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient.renderSky
import org.dimdev.dimdoors.network.client.ClientPacketListener.getAddonClient
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import org.dimdev.dimdoors.world.pocket.type.addon.environment.EnvironmentAddon
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.joml.Matrix4f

enum class DungeonDimensionEffect : DimensionEffect {
    INSTANCE;

    override fun renderSky(
        level: ClientLevel,
        ticks: Int,
        partialTick: Float,
        modelViewMatrix: Matrix4f,
        camera: Camera,
        projectionMatrix: Matrix4f,
        isFoggy: Boolean,
        setupFog: Runnable
    ): Boolean {
        val data = getEnvironmentAddon(level, camera.blockPosition)?.sky

        processSky(
            data,
            if (level.dimension() == ModDimensions.PERSONAL) 255 else 0,
            level,
            partialTick,
            modelViewMatrix,
            camera,
            projectionMatrix,
            isFoggy,
            setupFog
        )

        return true
    }

    override fun renderClouds(
        level: ClientLevel,
        ticks: Int,
        partialTick: Float,
        poseStack: PoseStack,
        camX: Double,
        camY: Double,
        camZ: Double,
        modelViewMatrix: Matrix4f,
        projectionMatrix: Matrix4f
    ): Boolean {
        val data = getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ))?.cloud ?: return true

        EnvironmentAddonClient.renderCloud(
            data,
            level,
            ticks,
            partialTick,
            poseStack,
            camX,
            camY,
            camZ,
            modelViewMatrix,
            projectionMatrix
        )

        return true
    }

    override fun renderWeather(
        level: ClientLevel,
        ticks: Int,
        partialTick: Float,
        lightTexture: LightTexture,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val data = getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ))?.weather ?: return true

        EnvironmentAddonClient.renderWeather(
            data,
            level,
            ticks,
            partialTick,
            lightTexture,
            camX,
            camY,
            camZ
        )

        return true
    }

    private fun getEnvironmentAddon(level: ClientLevel, pos: BlockPos): EnvironmentAddon? = getAddonClient(PocketAddons.ENVIRONMENT_ADDON, level, pos)

    private fun processSky(
        data: SkyData?,
        voidColor: Int,
        level: ClientLevel,
        partialTick: Float,
        modelViewMatrix: Matrix4f,
        camera: Camera,
        projectionMatrix: Matrix4f,
        isFoggy: Boolean,
        setupFog: Runnable
    ) {
//        setupFog.run();
        if (!isFoggy) {
            val fogtype = camera.fluidInCamera
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !(Minecraft.getInstance().levelRenderer as LevelRendererExtension).isMobEffectBlockingSky(
                    camera
                )
            ) {
                val poseStack = PoseStack()
                poseStack.mulPose(modelViewMatrix)

                if (data != null) {
                    renderSky(data, level, poseStack, projectionMatrix, partialTick, isFoggy, setupFog, camera)
                } else {
                    RenderSystem.enableBlend()
                    RenderSystem.depthMask(false)

                    LimboDimensionEffect.renderSkyBox(poseStack, voidColor)

                    RenderSystem.depthMask(true)
                    RenderSystem.disableBlend()
                }
            }
        }
    }
}
