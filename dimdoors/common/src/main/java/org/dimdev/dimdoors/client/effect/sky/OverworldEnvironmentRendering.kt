package org.dimdev.dimdoors.client.effect.sky

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.util.Mth
import org.dimdev.dimdoors.client.CloudRenderBuffer
import org.dimdev.dimdoors.client.effect.LevelRendererExtension
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.OverworldCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.OverWorldSkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.OverworldWeatherData
import org.joml.Matrix4f
import java.util.function.Supplier

object OverworldEnvironmentRendering {
    fun renderSky(
        info: OverWorldSkyData,
        level: ClientLevel,
        poseStack: PoseStack,
        projectionMatrix: Matrix4f,
        partialTick: Float,
        isFoggy: Boolean,
        skyFogSetup: Runnable,
        camera: Camera
    ) {
        val minecraft = Minecraft.getInstance()
        val levelRenderer = minecraft.levelRenderer

        val vec3 = info.correctedSkyColor
        val g = vec3.x.toFloat()
        val h = vec3.y.toFloat()
        val i = vec3.z.toFloat()
        FogRenderer.levelFogColor()
        val tesselator = Tesselator.getInstance()
        RenderSystem.depthMask(false)
        RenderSystem.setShaderColor(g, h, i, 1.0f)
        val shaderInstance = RenderSystem.getShader()
        levelRenderer.skyBuffer!!.bind()
        levelRenderer.skyBuffer!!.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance)
        VertexBuffer.unbind()
        RenderSystem.enableBlend()
        val fs = info.getSunriseColor(info.timeOfDay)
        if (fs != null) {
            RenderSystem.setShader(Supplier { GameRenderer.getPositionColorShader() })
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            poseStack.pushPose()
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0f))
            val j = if (Mth.sin(info.sunAngle) < 0.0f) 180.0f else 0.0f
            poseStack.mulPose(Axis.ZP.rotationDegrees(j))
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f))
            val k = fs[0]
            val l = fs[1]
            val m = fs[2]
            val matrix4f3 = poseStack.last().pose()
            val bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
            bufferBuilder.addVertex(matrix4f3, 0.0f, 100.0f, 0.0f).setColor(k, l, m, fs[3])
            val n = 16

            for (o in 0..16) {
                val p = o.toFloat() * (Math.PI.toFloat() * 2f) / 16.0f
                val q = Mth.sin(p)
                val r = Mth.cos(p)
                bufferBuilder.addVertex(matrix4f3, q * 120.0f, r * 120.0f, -r * 40.0f * fs[3])
                    .setColor(fs[0], fs[1], fs[2], 0.0f)
            }

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
            poseStack.popPose()
        }

        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        poseStack.pushPose()
        val j = 1.0f - info.rainLevel
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, j)
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f))
        poseStack.mulPose(Axis.XP.rotationDegrees(info.timeOfDay * 360.0f))
        val matrix4f4 = poseStack.last().pose()
        var l = 30.0f
        RenderSystem.setShader(Supplier { GameRenderer.getPositionTexShader() })
        RenderSystem.setShaderTexture(0, LevelRenderer.SUN_LOCATION)
        var bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder2.addVertex(matrix4f4, -l, 100.0f, -l).setUv(0.0f, 0.0f)
        bufferBuilder2.addVertex(matrix4f4, l, 100.0f, -l).setUv(1.0f, 0.0f)
        bufferBuilder2.addVertex(matrix4f4, l, 100.0f, l).setUv(1.0f, 1.0f)
        bufferBuilder2.addVertex(matrix4f4, -l, 100.0f, l).setUv(0.0f, 1.0f)
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow())
        l = 20.0f
        RenderSystem.setShaderTexture(0, LevelRenderer.MOON_LOCATION)
        val s = info.moonPhase
        val t = s % 4
        val n = s / 4 % 2
        val u = (t).toFloat() / 4.0f
        val p = (n).toFloat() / 2.0f
        val q = (t + 1).toFloat() / 4.0f
        val r = (n + 1).toFloat() / 2.0f
        bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder2.addVertex(matrix4f4, -l, -100.0f, l).setUv(q, r)
        bufferBuilder2.addVertex(matrix4f4, l, -100.0f, l).setUv(u, r)
        bufferBuilder2.addVertex(matrix4f4, l, -100.0f, -l).setUv(u, p)
        bufferBuilder2.addVertex(matrix4f4, -l, -100.0f, -l).setUv(q, p)
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow())
        val v = info.starBrightness * j
        if (v > 0.0f) {
            RenderSystem.setShaderColor(v, v, v, v)
            FogRenderer.setupNoFog()
            levelRenderer.starBuffer!!.bind()
            levelRenderer.starBuffer!!.drawWithShader(
                poseStack.last().pose(),
                projectionMatrix,
                GameRenderer.getPositionShader()
            )
            VertexBuffer.unbind()
            skyFogSetup.run()
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()
        RenderSystem.defaultBlendFunc()
        poseStack.popPose()
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f)

        //        TODO: Determine if this good fit for addon rendering

//        double d = minecraft.player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
//        if (d < (double) 0.0F) {
//            poseStack.pushPose();
//            poseStack.translate(0.0F, 12.0F, 0.0F);
//            levelRenderer.darkBuffer.bind();
//            levelRenderer.darkBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
//            VertexBuffer.unbind();
//            poseStack.popPose();
//        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.depthMask(true)
    }

    fun renderCloud(
        data: OverworldCloudData,
        level: ClientLevel?,
        ticks: Int,
        partialTick: Float,
        poseStack: PoseStack?,
        camX: Double,
        camY: Double,
        camZ: Double,
        modelViewMatrix: Matrix4f?,
        projectionMatrix: Matrix4f?
    ) {
        (Minecraft.getInstance().levelRenderer as CloudRenderBuffer).renderCloudBuffer(
            poseStack,
            modelViewMatrix,
            projectionMatrix,
            partialTick,
            ticks,
            camX,
            camY,
            camZ,
            data.cloudHeight,
            data.cloudColor
        )
    }

    fun renderWeather(
        data: OverworldWeatherData,
        level: ClientLevel?,
        ticks: Int,
        partialTick: Float,
        lightTexture: LightTexture?,
        camX: Double,
        camY: Double,
        camZ: Double
    ) {
        (Minecraft.getInstance().levelRenderer as LevelRendererExtension).renderWeather(
            lightTexture,
            partialTick,
            ticks,
            camX,
            camY,
            camZ,
            data.precepitation,
            data.rainLevel
        )
    }
}
